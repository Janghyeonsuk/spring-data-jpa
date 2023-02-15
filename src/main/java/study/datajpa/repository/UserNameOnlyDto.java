package study.datajpa.repository;

public class UserNameOnlyDto {

    private final String username;

    public UserNameOnlyDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
