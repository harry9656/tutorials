package com.baeldung.loginextrafieldssimple;

public interface UserRepository {

    User findUser(String username, String domain);

}
