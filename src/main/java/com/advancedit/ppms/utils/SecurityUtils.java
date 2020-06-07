package com.advancedit.ppms.utils;

import com.advancedit.ppms.models.user.Permission;
import com.advancedit.ppms.models.user.Role;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SecurityUtils {

    public static LoggedUserInfo getLoggedUserInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null){
            if (authentication.getPrincipal() instanceof LoggedUserInfo){
                return (LoggedUserInfo)authentication.getPrincipal();
            }
        }
        return null;
    }

    public static long getCurrentTenantId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null){
            if (authentication.getPrincipal() instanceof LoggedUserInfo){
                return ((LoggedUserInfo)authentication.getPrincipal()).getTenantId();
            }
        }
        return 0;
    }

    public static void hasRole(Role role){
        if (!isHasRole(role)) throw new AccessDeniedException(String.format("The role %s is needed", role.name()));
    }

    public static void hasAnyRole(Role... roles){
        if (!isHasAnyRole(roles))  throw new AccessDeniedException(
                String.format("one of the roles %s is needed", Stream.of(roles)
                        .map(Role::name).collect(Collectors.joining(", "))));
    }

    public static boolean isHasRole(Role role){
        LoggedUserInfo userInfo = getLoggedUserInfo();
        return userInfo != null && userInfo.getRoles() != null && userInfo.getRoles().contains(role);
    }

    public static boolean isHasAnyRole(Role... roles){
        LoggedUserInfo userInfo = getLoggedUserInfo();
        return userInfo != null && userInfo.getRoles() != null
                && Stream.of(roles).anyMatch(role -> userInfo.getRoles().contains(role));
    }


    public static boolean hasPermission(Permission permission){
        LoggedUserInfo userInfo = getLoggedUserInfo();
        return userInfo != null && userInfo.getPermissions() != null && userInfo.getPermissions().contains(permission);
    }
}
