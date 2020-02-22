package com.thunder_cut.processing.data;

/**
 * op --> 서버권한 부여 서버에서 쓰는 명령어를 사용가능하게 됨 + 클라의 기능,커맨드는 계속 유지
 * ignore --> 특정 사람 차단 (Image + Message)
 * blind --> 내 화면 안보여주기
 * set_name --> 닉네임 설정
 * <p>
 * kick, op --> 서버의 권한
 */
public enum CommandType {

    SET_NAME("/set_name", false),
    KICK("/kick", true),
    OP("/op", true),
    IGNORE("/ignore", false),
    BLIND("/blind", false);

    public final String command;
    public final boolean op;


    CommandType(String s, boolean b) {
        this.command = s;
        this.op = b;
    }

    private CommandType getCommand(String type) {
        for (CommandType commandType : CommandType.values()) {
            if (commandType.command.equals(type)) {
                return commandType;
            }
        }
        return null;
    }

    public static CommandType acceptable(String type, boolean op) {
        if (!op) {
            for (CommandType commandType : CommandType.values()) {
                if (commandType.command.equals(type) && !commandType.op) {
                    return commandType;
                }
            }
        } else {
            for (CommandType commandType : CommandType.values()) {
                if (commandType.command.equals(type)) {
                    return commandType;
                }
            }
        }
        return null;
    }

}
