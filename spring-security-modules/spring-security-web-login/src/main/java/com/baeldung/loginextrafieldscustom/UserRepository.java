package com.baeldung.loginextrafieldscustom;

public interface UserRepository {

    User findUser(String username, String domain);

}
