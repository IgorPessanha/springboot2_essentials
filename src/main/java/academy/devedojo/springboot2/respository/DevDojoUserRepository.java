package academy.devedojo.springboot2.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import academy.devedojo.springboot2.domain.DevDojoUser;

@Repository
public interface DevDojoUserRepository extends JpaRepository<DevDojoUser, Long>{
	
	DevDojoUser findByUsername(String username);
	
}
	