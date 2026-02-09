package com.smartcampus.tools;

import com.smartcampus.utils.PasswordUtil;

/**
 * 本地小工具：生成 BCrypt 密码哈希（用于初始化 SQL）。
 *
 * 用法示例：
 * mvn -q -DskipTests org.codehaus.mojo:exec-maven-plugin:3.1.0:java \
 *   -Dexec.mainClass=com.smartcampus.tools.PasswordHashTool \
 *   -Dexec.classpathScope=test \
 *   -Dexec.args="admin123"
 */
public class PasswordHashTool {

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.err.println("Usage: PasswordHashTool <plainPassword>");
            System.exit(2);
        }
        System.out.println(PasswordUtil.encode(args[0]));
    }
}

