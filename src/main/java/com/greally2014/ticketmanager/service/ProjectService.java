package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.ProjectRepository;
import com.greally2014.ticketmanager.dto.project.ProjectAddUserDto;
import com.greally2014.ticketmanager.dto.project.ProjectCreationDto;
import com.greally2014.ticketmanager.dto.project.ProjectDetailsDto;
import com.greally2014.ticketmanager.dto.project.ProjectDto;
import com.greally2014.ticketmanager.dto.user.UserProfileDto;
import com.greally2014.ticketmanager.entity.*;
import com.greally2014.ticketmanager.exception.ProjectNotFoundException;
import com.greally2014.ticketmanager.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final CustomUserDetailsService customUserDetailsService;

    private final ProjectManagerService projectManagerService;

    private final DeveloperService developerService;

    private final SubmitterService submitterService;

    private final UsersProjectsService usersProjectsService;

    private final ProjectRepository projectRepository;

    public ProjectService(CustomUserDetailsService customUserDetailsService,
                          ProjectManagerService projectManagerService,
                          DeveloperService developerService,
                          SubmitterService submitterService,
                          UsersProjectsService usersProjectsService,
                          ProjectRepository projectRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.projectManagerService = projectManagerService;
        this.developerService = developerService;
        this.submitterService = submitterService;
        this.usersProjectsService = usersProjectsService;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Project findById(Long id) throws ProjectNotFoundException {
        Optional<Project> projectOptional = projectRepository.findById(id);
        projectOptional.orElseThrow(() -> new ProjectNotFoundException("Project not found. Id: " + id));
        return projectOptional.get();
    }

    @Transactional
    public List<Project> findAllByRole(String username) {
        User user = customUserDetailsService.loadUserByUsername(username).getUser();
        List<Project> projects;

        if (user.getRoles().stream().anyMatch(o -> o.getName().equals("ROLE_PROJECT_MANAGER"))) {
            projects = projectManagerService.findProjects(username);
        } else {
            projects = projectRepository.findAll();
        }

        projects.sort(Comparator.comparing(Project::getTitle));

        return projects;
    }

    @Transactional
    public void add(ProjectCreationDto projectCreationDto, String username) throws UserNotFoundException {
        Project project = new Project(
                projectCreationDto.getProjectDto().getTitle(),
                projectCreationDto.getProjectDto().getDescription(),
                LocalDate.now()
        );

        GeneralManager creator = (GeneralManager) customUserDetailsService.loadUserByUsername(username).getUser();
        project.setCreator(creator);

        List<Long> selectedProjectManagerIds = projectCreationDto.getProjectManagerDtoList().stream()
                .filter(UserProfileDto::getFlag)
                .map(UserProfileDto::getId)
                .collect(Collectors.toList());

        for (Long id : selectedProjectManagerIds) {
            customUserDetailsService.findById(id);
        }

        List<UsersProjects> usersProjects = projectManagerService.findAll(selectedProjectManagerIds).stream()
                .map(o -> (new UsersProjects(o, project, LocalDate.now())))
                .collect(Collectors.toList());

        project.setUsersProjects(usersProjects);

        projectRepository.save(project);
    }

    @Transactional
    public ProjectCreationDto getCreationDto() {

        return new ProjectCreationDto(new ProjectDto(), projectManagerService.findProfileDtoList());
    }

    @Transactional
    public void updateFields(ProjectDto projectDto) throws ProjectNotFoundException {
        Project project = findById(projectDto.getId());
        project.setTitle(projectDto.getTitle());
        project.setDescription(projectDto.getDescription());
    }

    @Transactional
    public void delete(Long id) throws ProjectNotFoundException {
        findById(id);
        projectRepository.deleteById(id);
    }

    @Transactional
    public ProjectDto getDto(Long id) throws ProjectNotFoundException {

        return new ProjectDto(findById(id));
    }

    @Transactional
    public List<UserProfileDto> findAllUserDto(Long id, String role) throws ProjectNotFoundException {
        List<UserProfileDto> userProfileDtoList = findById(id).getUsersProjects().stream()
                .map(UsersProjects::getUser)
                .filter(o -> o.getRoles().stream()
                        .anyMatch(r -> r.getName().equals(role))
                )
                .map(UserProfileDto::new)
                .collect(Collectors.toList());

        userProfileDtoList.forEach(o -> o.setUsersProjects(
                usersProjectsService.findOne(o.getId(), id)));

        return userProfileDtoList;

    }

    @Transactional
    public List<Ticket> findAllTickets(Long id) throws ProjectNotFoundException {
        return findById(id).getTickets();
    }

    @Transactional
    public ProjectDetailsDto getDetailsDto(Long id) throws ProjectNotFoundException {
        return new ProjectDetailsDto(
                getDto(id),
                findAllUserDto(id, "ROLE_PROJECT_MANAGER"),
                findAllUserDto(id, "ROLE_DEVELOPER"),
                findAllUserDto(id, "ROLE_SUBMITTER"),
                findAllTickets(id)
        );
    }

    @Transactional
    public void kickUser(Long userId, Long projectId) throws UserNotFoundException, ProjectNotFoundException {
        customUserDetailsService.findById(userId);
        findById(projectId);
        usersProjectsService.delete(userId, projectId);
    }

    @Transactional
    public List<UserProfileDto> findAllUserDtoNotAdded(Long id, Long roleIdentifier) throws ProjectNotFoundException {

        List<UserProfileDto> alreadyAdded;
        List<UserProfileDto> userDtoList;
        ProjectDetailsDto projectDetailsDto = getDetailsDto(id);

        if (roleIdentifier == 1) {
            alreadyAdded = projectDetailsDto.getProjectManagerDtoList();
            userDtoList = projectManagerService.findProfileDtoList();

        } else if (roleIdentifier == 2) {
            alreadyAdded = projectDetailsDto.getDeveloperDtoList();
            userDtoList = developerService.findProfileDtoList();

        } else {
            alreadyAdded = projectDetailsDto.getSubmitterDtoList();
            userDtoList = submitterService.findProfileDtoList();
        }

        return removeAlreadyAdded(alreadyAdded, userDtoList);
    }

    public List<UserProfileDto> removeAlreadyAdded(List<UserProfileDto> alreadyAdded,
                                                   List<UserProfileDto> userDtoList) {
        List<UserProfileDto> userDtoListCopy = new ArrayList<>();

        for (UserProfileDto userProfileDto : userDtoList) {
            String username = userProfileDto.getUsername();
            for (UserProfileDto test : alreadyAdded) {
                if (username.equals(test.getUsername())) {
                    userDtoListCopy.add(userProfileDto);
                }
            }
        }

        for (UserProfileDto userProfileDto : userDtoListCopy) {
            userDtoList.remove(userProfileDto);
        }

        return userDtoList;
    }

    @Transactional
    public void addUsers(ProjectAddUserDto projectAddUserDto) throws UserNotFoundException, ProjectNotFoundException {
        Project project = findById(projectAddUserDto.getProjectDto().getId());

        for (UserProfileDto userProfileDto : projectAddUserDto.getUserDtoList()) {
            if (userProfileDto.getFlag()) {
                User user = customUserDetailsService.findById(userProfileDto.getId());
                usersProjectsService.add(user, project);
            }
        }
    }

}
