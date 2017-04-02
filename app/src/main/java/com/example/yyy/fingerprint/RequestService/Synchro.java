package com.example.yyy.fingerprint.RequestService;

/**
 * Created by admin on 2017/2/25.
 */

public class Synchro {
    private String guid;
    private String file_path;
    private String nickname;
    private String authority_number;
    private String operate_date;
    private String operate_time;
    private String isPermit;//过来的都是No，改为yes返回//是否允许
    private String isSend;

    public Synchro(String guid, String file_path, String nickname, String authority_number, String operate_date, String operate_time, String isPermit, String isSend) {
        this.guid = guid;
        this.file_path = file_path;
        this.nickname = nickname;
        this.authority_number = authority_number;
        this.operate_date = operate_date;
        this.operate_time = operate_time;
        this.isPermit = isPermit;
        this.isSend = isSend;
    }

    public Synchro(String guid, String file_path, String authority_number, String operate_date, String operate_time, String isPermit, String isSend) {
        this.guid = guid;
        this.file_path = file_path;
        this.authority_number = authority_number;
        this.operate_date = operate_date;
        this.operate_time = operate_time;
        this.isPermit = isPermit;
        this.isSend = isSend;
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

    public String getOperate_date() {
        return operate_date;
    }

    public void setOperate_date(String operate_date) {
        this.operate_date = operate_date;
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

    public String getIsSend() {return isSend;}

    public void setIsSend(String isSend) {this.isSend = isSend;}
}
