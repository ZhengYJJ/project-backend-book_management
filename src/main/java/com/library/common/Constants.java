package com.library.common;

/**
 * 业务常量定义。
 */
public final class Constants {

    private Constants() {
    }

    /** Session 中存放登录用户的 key */
    public static final String SESSION_USER = "loginUser";

    /** 馆藏副本状态（对应 book_copy.status，FR-BOOK-005） */
    public static final class CopyStatus {
        public static final int IN_STOCK = 0;   // 在馆
        public static final int BORROWED = 1;   // 已借出
        public static final int LOST = 3;       // 遗失
        public static final int REPAIRING = 4;  // 修复中
        public static final int SCRAPPED = 5;   // 已注销

        private CopyStatus() {
        }

        public static String nameOf(int status) {
            switch (status) {
                case IN_STOCK: return "在馆";
                case BORROWED: return "已借出";
                case LOST: return "遗失";
                case REPAIRING: return "修复中";
                case SCRAPPED: return "已注销";
                default: return "未知(" + status + ")";
            }
        }
    }

    /** 借阅记录状态（对应 loan.status） */
    public static final class LoanStatus {
        public static final int BORROWED = 0;  // 在借
        public static final int RETURNED = 1;  // 已还
        public static final int OVERDUE = 2;   // 逾期（在借）

        private LoanStatus() {
        }

        public static String nameOf(int status) {
            switch (status) {
                case BORROWED: return "在借";
                case RETURNED: return "已还";
                case OVERDUE: return "逾期";
                default: return "未知(" + status + ")";
            }
        }
    }

    /** 读者状态（对应 reader.status，FR-READER-003） */
    public static final class ReaderStatus {
        public static final int NORMAL = 0;
        public static final int LOST = 1;        // 挂失
        public static final int SUSPENDED = 2;   // 停借
        public static final int CANCELLED = 3;   // 注销

        private ReaderStatus() {
        }

        public static String nameOf(int status) {
            switch (status) {
                case NORMAL: return "正常";
                case LOST: return "挂失";
                case SUSPENDED: return "停借";
                case CANCELLED: return "注销";
                default: return "未知(" + status + ")";
            }
        }
    }

    /** 系统用户角色（FR-SYS-001） */
    public static final class Role {
        public static final int ADMIN = 1;        // 系统管理员
        public static final int CATALOGER = 2;    // 编目馆员
        public static final int CIRCULATION = 3;  // 流通馆员
        public static final int SUPERVISOR = 4;   // 主管

        private Role() {
        }

        public static String nameOf(int role) {
            switch (role) {
                case ADMIN: return "系统管理员";
                case CATALOGER: return "编目馆员";
                case CIRCULATION: return "流通馆员";
                case SUPERVISOR: return "主管";
                default: return "未知(" + role + ")";
            }
        }
    }

    /** 系统用户状态 */
    public static final class UserStatus {
        public static final int ACTIVE = 0;
        public static final int DISABLED = 1;

        private UserStatus() {
        }
    }
}
