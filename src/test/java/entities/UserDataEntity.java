package entities;

import org.apache.http.client.utils.DateUtils;

import java.util.Date;

import static consts.DateFormat.STRING_FORMAT;

public class UserDataEntity {
    private String username;
    private String email;
    private String password;
    private String created_at;
    private String updated_at;
    private Long id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreated_at() {
        return created_at;
    }

    public Date getParsedCreated_at() {
        return DateUtils.parseDate(created_at, STRING_FORMAT);
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public Date getParsedUpdated_at() {
        return DateUtils.parseDate(created_at, STRING_FORMAT);
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
