package com.backend.vertwo.entity.user;

public class AuthorityConstants {
    public static final String AUTHORITIES = "authorities";
    public static final String ROLE = "role";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String AUTHORITY_DELIMITER = ",";
    public static final String USER_AUTHORITY = "document:read,document:create,document:update,document:delete";
    public static final String STAFF_AUTHORITY = "document:read,document:create,document:update,document:delete";
    public static final String MANAGER_AUTHORITY = "user:create,user:read,user:update,document:read,document:create,document:update,document:delete";
    public static final String ADMIN_AUTHORITY = "user:create,user:read,user:update,user:delete,document:read,document:create,document:update,document:delete";
}
