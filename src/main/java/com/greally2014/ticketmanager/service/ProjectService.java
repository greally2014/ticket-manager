package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.ProjectRepository;
import com.greally2014.ticketmanager.dto.*;
import com.greally2014.ticketmanager.entity.*;
import com.greally2014.ticketmanager.exception.ProjectNotFoundException;
import com.greally2014.ticketmanager.exception.UserNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    public List<Project> findAllByUsernameOrderByTitle(String username) {

        List<Project> projects;

        try {
            User user = customUserDetailsService.loadUserByUsername(username).getUser();

            if (user.getRoles().stream().anyMatch(o -> o.getName().equals("ROLE_PROJECT_MANAGER"))) {
                projects = projectManagerService.findProjects(username);
            } else {
                projects = projectRepository.findAll();
            }

            projects.sort(Comparator.comparing(Project::getTitle));

            return projects;

        } catch (UsernameNotFoundException e) {
            e.printStackTrace();

            throw e;
        }
    }

    @Transactional
    public void add(ProjectCreationDto projectCreationDto) {
        Project project = new Project(
                projectCreationDto.getProjectDto().getTitle(),
                projectCreationDto.getProjectDto().getDescription()
        );

        project.setCreator(
                (GeneralManager) customUserDetailsService.loadUserByUsername(
                        SecurityContextHolder.getContext().getAuthentication().getName()).getUser()
        );

        List<Long> selectedProjectManagerIds = projectCreationDto.getProjectManagerDtoList().stream()
                .filter(UserProfileDto::getFlag)
                .map(UserProfileDto::getId)
                .collect(Collectors.toList());

        List<UsersProjects> usersProjects = projectManagerService.findAllById(selectedProjectManagerIds).stream()
                .map(o -> (new UsersProjects(o, project)))
                .collect(Collectors.toList());

        project.setUsersProjects(usersProjects);

        projectRepository.save(project);
    }

    @Transactional
    public void updateFields(ProjectDto projectDto) throws ProjectNotFoundException {
        Project project = findById(projectDto.getId());
        project.setTitle(projectDto.getTitle());
        project.setDescription(projectDto.getDescription());
    }

    @Transactional
    public void delete(Long id) throws ProjectNotFoundException {
        // check if username exists and handle exception / have denied access redirect / error handler
        // check if project already deleted
        try {
            findById(id);
            projectRepository.deleteById(id);

        } catch (ProjectNotFoundException e) {
            e.printStackTrace();

            throw e;
        }
    }

    @Transactional
    public ProjectDto getDto(Long id) throws ProjectNotFoundException {
        return new ProjectDto(findById(id));
    }

    @Transactional
    public List<UserProfileDto> findAllUserProfileDto(Long id, String role) throws ProjectNotFoundException {
        try {
            List<UserProfileDto> userProfileDtoList = findById(id).getUsersProjects().stream()
                    .map(UsersProjects::getUser)
                    .filter(o -> o.getRoles().stream()
                            .anyMatch(r -> r.getName().equals(role))
                    )
                    .map(UserProfileDto::new)
                    .collect(Collectors.toList());

            userProfileDtoList.forEach(o -> o.setUsersProjects(
                    usersProjectsService.findByUserIdAndProjectId(o.getId(), id)));

            return userProfileDtoList;

        } catch (ProjectNotFoundException e) {
            e.printStackTrace();

            throw e;
        }

    }

    @Transactional
    public List<Ticket> findAllTickets(Long id) throws ProjectNotFoundException {
        try {

            return findById(id).getTickets();

        } catch (ProjectNotFoundException e) {
            e.printStackTrace();

            throw e;
        }
    }

    @Transactional
    public ProjectDetailsDto getDetailsDto(Long id) throws ProjectNotFoundException {
        try {

            return new ProjectDetailsDto(
                    getDto(id),
                    findAllUserProfileDto(id, "ROLE_PROJECT_MANAGER"),
                    findAllUserProfileDto(id, "ROLE_DEVELOPER"),
                    findAllUserProfileDto(id, "ROLE_SUBMITTER"),
                    findAllTickets(id)
            );

        } catch (ProjectNotFoundException e) {
            e.printStackTrace();

            throw e;
        }
    }

    @Transactional
    public void kickUser(Long userId, Long projectId) {
        try {
            usersProjectsService.deleteByUserIdAndProjectId(userId, projectId);

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }

    @Transactional
    public List<UserProfileDto> findAllUserProfileDtoNotAdded(Long id, Long roleIdentifier) throws ProjectNotFoundException {
        try {
            findById(id);
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


        } catch (ProjectNotFoundException e) {
            e.printStackTrace();

            throw e;
        }
    }

    @Transactional
    public void addUsers(ProjectAddUserDto projectAddUserDto) throws ProjectNotFoundException, UserNotFoundException {
        try {
            Project project = findById(projectAddUserDto.getProjectDto().getId());

            for (UserProfileDto userProfileDto : projectAddUserDto.getUserDtoList()) {
                if (userProfileDto.getFlag()) {
                    User user = customUserDetailsService.findById(userProfileDto.getId());

                    usersProjectsService.add(user, project);
                }
            }

        } catch (ProjectNotFoundException e) {
            e.printStackTrace();

            throw e;
        }
    }

}
