package com.example.yyy.fingerprint.FolderManage;

/**
 * Created by admin on 2017/2/25.
 */

public class Authority {
    private String guid;//电脑id
    private String file_path;//文件目录
    private String nickname;
    private String authority_number;//限权管理

    public Authority(String guid, String file_path, String authority_number) {
        this.guid = guid;
        this.file_path = file_path;
        this.authority_number = authority_number;
    }

    public Authority(String guid, String file_path, String nickname,  String authority_number) {
        this.guid = guid;
        this.nickname = nickname;
        this.file_path = file_path;
        this.authority_number = authority_number;
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

}
