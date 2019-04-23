package com.dev.mobile.vpn.data.model;

public class CountryResponse {

    private String name;
    private String code;
    private String flag;
    private String servers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }

    @Override
    public String toString() {
        return "CountryResponse{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", flag='" + flag + '\'' +
                ", servers='" + servers + '\'' +
                '}';
    }
}
