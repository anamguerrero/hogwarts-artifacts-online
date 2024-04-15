package edu.tcu.cs.hogwartsartifactsonline.hogwartsuser;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface UserRepository extends JpaRepository<HogwartsUser, Integer> {
    //Optional wraps an object
    Optional<HogwartsUser> findByUsername(String username);
}