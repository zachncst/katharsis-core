package io.katharsis.resource.mock.repository;

import io.katharsis.queryParams.RequestParams;
import io.katharsis.repository.RelationshipRepository;
import io.katharsis.resource.mock.models.Project;
import io.katharsis.resource.mock.models.Task;
import io.katharsis.resource.mock.repository.util.Relation;

import java.util.*;

public class TaskToProjectRepository implements RelationshipRepository<Task, Long, Project, Long> {

    // Used ThreadLocal in case of switching to TestNG and using concurrent tests
    private static final ThreadLocal<Set<Relation<Task>>> THREAD_LOCAL_REPOSITORY = new ThreadLocal<Set<Relation<Task>>>() {
        @Override
        protected Set<Relation<Task>> initialValue() {
            return new HashSet<>();
        }
    };

    @Override
    public void setRelation(Task source, Long targetId, String fieldName) {
        removeRelations(fieldName);
        if (targetId != null) {
            THREAD_LOCAL_REPOSITORY.get().add(new Relation<>(source, targetId, fieldName));
        }
    }

    @Override
    public void setRelations(Task source, Iterable<Long> targetIds, String fieldName) {
        removeRelations(fieldName);
        if (targetIds != null) {
            for (Long targetId : targetIds) {
                THREAD_LOCAL_REPOSITORY.get().add(new Relation<>(source, targetId, fieldName));
            }
        }
    }

    @Override
    public void addRelations(Task source, Iterable<Long> targetIds, String fieldName) {
        targetIds.forEach(targetId ->
                THREAD_LOCAL_REPOSITORY.get().add(new Relation<>(source, targetId, fieldName))
        );
    }

    @Override
    public void removeRelations(Task source, Iterable<Long> targetIds, String fieldName) {
        targetIds.forEach(targetId -> {
            Iterator<Relation<Task>> iterator = THREAD_LOCAL_REPOSITORY.get().iterator();
            while (iterator.hasNext()) {
                Relation<Task> next = iterator.next();
                if (next.getFieldName().equals(fieldName) && next.getTargetId().equals(targetId)) {
                    iterator.remove();
                }
            }
        });
    }

    private void removeRelations(String fieldName) {
        Iterator<Relation<Task>> iterator = THREAD_LOCAL_REPOSITORY.get().iterator();
        while (iterator.hasNext()) {
            Relation<Task> next = iterator.next();
            if (next.getFieldName().equals(fieldName)) {
                iterator.remove();
            }
        }
    }

    @Override
    public Project findOneTarget(Long sourceId, String fieldName, RequestParams requestParams) {
        for (Relation<Task> relation : THREAD_LOCAL_REPOSITORY.get()) {
            if (relation.getSource().getId().equals(sourceId) &&
                relation.getFieldName().equals(fieldName)) {
                Project project = new Project();
                project.setId((Long) relation.getTargetId());
                return project;
            }
        }
        return null;
    }

    @Override
    public Iterable<Project> findManyTargets(Long sourceId, String fieldName, RequestParams requestParams) {
        List<Project> projects = new LinkedList<>();
        THREAD_LOCAL_REPOSITORY
            .get()
            .stream()
            .filter(relation -> relation.getSource()
                .getId().equals(sourceId) && relation.getFieldName().equals(fieldName)).forEach(relation -> {
            Project project = new Project();
            project.setId((Long) relation.getTargetId());
            projects.add(project);
        });
        return projects;
    }
}
