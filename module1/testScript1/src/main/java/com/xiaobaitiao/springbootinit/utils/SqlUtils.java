package com.xiaobaitiao.springbootinit.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * SQL 工具
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
public class SqlUtils {

    /**
     * 仅允许 Java/SQL 常见标识符，以及至多一级“表.字段”写法。
     */
    private static final String SAFE_SORT_FIELD_PATTERN =
            "^[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*)?$";

    /**
     * 校验排序字段是否合法（防止 SQL 注入）
     *
     * @param sortField
     * @return
     */
    public static boolean validSortField(String sortField) {
        if (StringUtils.isBlank(sortField)) {
            return false;
        }
        return sortField.matches(SAFE_SORT_FIELD_PATTERN);
    }
}
