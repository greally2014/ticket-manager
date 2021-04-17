package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.ProjectRepository;
import com.greally2014.ticketmanager.dao.UsersProjectsRepository;
import com.greally2014.ticketmanager.dto.ProjectCreationDto;
import com.greally2014.ticketmanager.dto.ProjectDetailsDto;
import com.greally2014.ticketmanager.dto.ProjectDto;
import com.greally2014.ticketmanager.dto.UserProfileDto;
import com.greally2014.ticketmanager.entity.*;
import com.greally2014.ticketmanager.exception.ProjectNotFoundException;
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

    private final ProjectRepository projectRepository;

    private final UsersProjectsRepository usersProjectsRepository;

    public ProjectService(CustomUserDetailsService customUserDetailsService,
                          ProjectManagerService projectManagerService,
                          ProjectRepository projectRepository,
                          UsersProjectsRepository usersProjectsRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.projectManagerService = projectManagerService;
        this.projectRepository = projectRepository;
        this.usersProjectsRepository = usersProjectsRepository;
    }

    @Transactional
    public Project findProjectById(Long id) throws ProjectNotFoundException {
        Optional<Project> projectOptional = projectRepository.findById(id);
        projectOptional.orElseThrow(() -> new ProjectNotFoundException("Project not found. Id: " + id));
        return projectOptional.get();
    }

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
        Project project = findProjectById(projectDto.getId());
        project.setTitle(projectDto.getTitle());
        project.setDescription(projectDto.getDescription());
    }

    @Transactional
    public void delete(Long id) throws ProjectNotFoundException {
        // check if username exists and handle exception / have denied access redirect / error handler
        // check if project already deleted
        try {
            findProjectById(id);
            projectRepository.deleteById(id);

        } catch (ProjectNotFoundException e) {
            e.printStackTrace();

            throw e;
        }
    }

    @Transactional
    public ProjectDto getDto(Long id) throws ProjectNotFoundException {
        return new ProjectDto(findProjectById(id));
    }

    @Transactional
    public List<UserProfileDto> findAllUserProfileDtoByRole(Long id, String role) throws ProjectNotFoundException {

        try {

            List<UserProfileDto> userProfileDtoList = findProjectById(id).getUsersProjects().stream()
                    .map(UsersProjects::getUser)
                    .filter(o -> o.getRoles().stream()
                            .anyMatch(r -> r.getName().equals(role))
                    )
                    .map(UserProfileDto::new)
                    .collect(Collectors.toList());

            userProfileDtoList.forEach(o -> o.setUsersProjects(usersProjectsRepository.findByUserIdAndProjectId(o.getId(), id)));

            return userProfileDtoList;

        } catch (ProjectNotFoundException e) {
            e.printStackTrace();

            throw e;
        }

    }

    @Transactional
    public List<Ticket> findAllTickets(Long id) throws ProjectNotFoundException {

        try {
            return findProjectById(id).getTickets();

        } catch (ProjectNotFoundException e) {
            e.printStackTrace();

            throw e;
        }
    }

    public ProjectDetailsDto getDetailsDto(Long id) throws ProjectNotFoundException {

        try {
            return new ProjectDetailsDto(
                    getDto(id),
                    findAllUserProfileDtoByRole(id, "ROLE_PROJECT_MANAGER"),
                    findAllUserProfileDtoByRole(id, "ROLE_DEVELOPER"),
                    findAllUserProfileDtoByRole(id, "ROLE_SUBMITTER"),
                    findAllTickets(id)
            );

        } catch (ProjectNotFoundException e) {
            e.printStackTrace();

            throw e;
        }
    }

    public void kickUser(Long userId, Long projectId) {
        try {
            usersProjectsRepository.deleteAllByUserIdAndProjectId(userId, projectId);

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }
}
