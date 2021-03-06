package com.sequenceiq.cloudbreak.workspace.authorization;

import static java.lang.String.format;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.sequenceiq.authorization.resource.AuthorizationResource;
import com.sequenceiq.authorization.service.PermissionChecker;
import com.sequenceiq.cloudbreak.workspace.model.Workspace;
import com.sequenceiq.cloudbreak.workspace.repository.check.CheckPermissionsByWorkspace;
import com.sequenceiq.authorization.resource.ResourceAction;

@Component
public class WorkspacePermissionChecker implements PermissionChecker<CheckPermissionsByWorkspace> {

    @Inject
    private PermissionCheckingUtils permissionCheckingUtils;

    @Override
    public <T extends Annotation> Object checkPermissions(T rawMethodAnnotation, AuthorizationResource resource, String userCrn,
            ProceedingJoinPoint proceedingJoinPoint, MethodSignature methodSignature) {
        Long workspaceId = getWorkspaceId(rawMethodAnnotation, proceedingJoinPoint);
        ResourceAction action = getAction(rawMethodAnnotation);
        return permissionCheckingUtils.checkPermissionsByWorkspaceIdForUserAndProceed(resource, userCrn,
                workspaceId, action, proceedingJoinPoint, methodSignature);
    }

    private <T extends Annotation> Long getWorkspaceId(T rawMethodAnnotation, ProceedingJoinPoint proceedingJoinPoint) {
        CheckPermissionsByWorkspace methodAnnotation = (CheckPermissionsByWorkspace) rawMethodAnnotation;
        int workspaceIndex = methodAnnotation.workspaceIndex();
        int length = proceedingJoinPoint.getArgs().length;
        validateWorkspaceIndex(proceedingJoinPoint, workspaceIndex, length);
        return ((Workspace) proceedingJoinPoint.getArgs()[workspaceIndex]).getId();
    }

    private void validateWorkspaceIndex(JoinPoint proceedingJoinPoint, int workspaceIndex, int length) {
        permissionCheckingUtils.validateIndex(workspaceIndex, length, "workspaceIndex");
        Object workspace = proceedingJoinPoint.getArgs()[workspaceIndex];
        if (!(workspace instanceof Workspace)) {
            throw new IllegalArgumentException(format("Type of workspace should be %s!", Workspace.class.getCanonicalName()));
        }
    }

    private <T extends Annotation> ResourceAction getAction(T rawMethodAnnotation) {
        return ((CheckPermissionsByWorkspace) rawMethodAnnotation).action();
    }

    @Override
    public Class<CheckPermissionsByWorkspace> supportedAnnotation() {
        return CheckPermissionsByWorkspace.class;
    }
}
