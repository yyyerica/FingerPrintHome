package com.example.yyy.fingerprint.FolderHistory;

/**
 * Created by admin on 2017/2/25.
 */

public class History {
    private String guid;
    private String file_path;
    private String nickname;
    private String authority_number;//权限
    private String operate_time;
    private String isPermit;
    private String isCheck;

    public History(String guid, String file_path, String nickname, String authority_number, String operate_time, String isPermit, String isCheck) {
        this.guid = guid;
        this.file_path = file_path;
        this.nickname = nickname;
        this.authority_number = authority_number;
        this.operate_time = operate_time;
        this.isPermit = isPermit;
        this.isCheck = isCheck;
    }

    public History(String guid, String file_path, String authority_number, String operate_time, String isPermit, String isCheck) {
        this.guid = guid;
        this.file_path = file_path;
        this.authority_number = authority_number;
        this.operate_time = operate_time;
        this.isPermit = isPermit;
        this.isCheck = isCheck;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getAuthority_number() {
        return authority_number;
    }

    public void setAuthority_number(String authority_number) {
        this.authority_number = authority_number;
    }

    public String getOperate_time() {
        return operate_time;
    }

    public void setOperate_time(String operate_time) {
        this.operate_time = operate_time;
    }

    public String getIsPermit() {
        return isPermit;
    }

    public void setIsPermit(String isPermit) {
        this.isPermit = isPermit;
    }

    public String getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(String isCheck) {
        this.isCheck = isCheck;
    }
}
