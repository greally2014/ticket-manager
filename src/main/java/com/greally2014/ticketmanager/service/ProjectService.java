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

        // if user is a project manager, fetch all projects from the project manager service
        if (user.getRoles().stream().anyMatch(o -> o.getName().equals("ROLE_PROJECT_MANAGER"))) {
            projects = projectManagerService.findProjects(username);
        } else { // if the user is a general manager, fetch everything
            projects = projectRepository.findAll();
        }

        projects.sort(Comparator.comparing(Project::getTitle)); // sorts project objects based on title

        return projects;
    }

    @Transactional
    public void add(ProjectCreationDto projectCreationDto, String username) throws UserNotFoundException {
        Project project = new Project(
                projectCreationDto.getProjectDto().getTitle(),
                projectCreationDto.getProjectDto().getDescription(),
                LocalDate.now()
        );
        // only general managers can create projects
        GeneralManager creator = (GeneralManager) customUserDetailsService.loadUserByUsername(username).getUser();
        project.setCreator(creator);

        // collects the ids of all selected project managers
        List<Long> selectedProjectManagerIds = projectCreationDto.getProjectManagerDtoList().stream()
                .filter(UserProfileDto::getFlag)
                .map(UserProfileDto::getId)
                .collect(Collectors.toList());

        // throws UserNotFoundException if a selected user is not in the database
        for (Long id : selectedProjectManagerIds) {
            customUserDetailsService.findById(id);
        }

        List<UsersProjects> usersProjects = projectManagerService.findAll(selectedProjectManagerIds).stream()
                .map(o -> (new UsersProjects(o, project, LocalDate.now()))) // date created
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
        findById(id); // throws ProjectNotFoundException if empty
        projectRepository.deleteById(id);
    }

    @Transactional
    public ProjectDto getDto(Long id) throws ProjectNotFoundException {
        return new ProjectDto(findById(id));
    }

    /**
     *
     * @param id project id
     * @param role project manager, developer, or submitter
     * @return a list of all users of the supplied role that are working on the project with the supplied id
     * @throws ProjectNotFoundException
     */
    @Transactional
    public List<UserProfileDto> findAllUserDtoByRole(Long id, String role) throws ProjectNotFoundException {
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
                findAllUserDtoByRole(id, "ROLE_PROJECT_MANAGER"),
                findAllUserDtoByRole(id, "ROLE_DEVELOPER"),
                findAllUserDtoByRole(id, "ROLE_SUBMITTER"),
                findAllTickets(id)
        );
    }

    @Transactional
    public void kickUser(Long userId, Long projectId) throws UserNotFoundException, ProjectNotFoundException {
        customUserDetailsService.findById(userId);
        findById(projectId);
        usersProjectsService.delete(userId, projectId);
    }

    /**
     * Returns a list of all users of the supplied role
     * that are not assigned to project of the supplied id
     *
     * This method is used to provide all users of a particular role
     * eligible to be added by checkbox to the project
     */
    @Transactional
    public List<UserProfileDto> findAllUserDtoNotAdded(Long id, Long roleIdentifier) throws ProjectNotFoundException {

        List<UserProfileDto> alreadyAdded; // list of users of a role already assigned
        List<UserProfileDto> userDtoList; // list of all application users of a role
        ProjectDetailsDto projectDetailsDto = getDetailsDto(id);

        // project managers
        if (roleIdentifier == 1) {
            alreadyAdded = projectDetailsDto.getProjectManagerDtoList();
            userDtoList = projectManagerService.findProfileDtoList();

            // developers
        } else if (roleIdentifier == 2) {
            alreadyAdded = projectDetailsDto.getDeveloperDtoList();
            userDtoList = developerService.findProfileDtoList();

            // submitters
        } else {
            alreadyAdded = projectDetailsDto.getSubmitterDtoList();
            userDtoList = submitterService.findProfileDtoList();
        }

        // removes users already assigned from the list of all users
        return removeAlreadyAdded(alreadyAdded, userDtoList);
    }

    //
    @Transactional
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
            if (userProfileDto.getFlag()) { // if selected
                User user = customUserDetailsService.findById(userProfileDto.getId());
                usersProjectsService.add(user, project);
            }
        }
    }

}
