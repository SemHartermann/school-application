package co.inventorsoft.academy.schoolapplication.repository;

import co.inventorsoft.academy.schoolapplication.entity.Post;
import co.inventorsoft.academy.schoolapplication.entity.Reaction;
import co.inventorsoft.academy.schoolapplication.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    Optional<Reaction> findByPostAndUser(Post post, User user);

    List<Reaction> findByPost(Post post);
}