package com.example.yyy.fingerprint.FolderManage;

/**
 * Created by admin on 2017/2/25.
 */

public class Authority {
    private String cpu_id;//电脑id
    private String file_path;//文件目录
    private String authority_number;//限权管理

    public Authority(String cpu_id, String file_path, String authority_number) {
        this.cpu_id = cpu_id;
        this.file_path = file_path;
        this.authority_number = authority_number;
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

}
