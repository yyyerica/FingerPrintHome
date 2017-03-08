package com.example.yyy.fingerprint.FolderHistory;

/**
 * Created by admin on 2017/2/25.
 */

public class History {
    private String cpu_id;
    private String file_path;
    private String authority_number;//权限
    private String operate_time;
    private String isPermit;
    private String isCheck;

    public History(String cpu_id, String file_path, String authority_number, String operate_time, String isPermit, String isCheck) {
        this.cpu_id = cpu_id;
        this.file_path = file_path;
        this.authority_number = authority_number;
        this.operate_time = operate_time;
        this.isPermit = isPermit;
        this.isCheck = isCheck;
    }

    public String getCpu_id() {
        return cpu_id;
    }

    public void setCpu_id(String cpu_id) {
        this.cpu_id = cpu_id;
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
