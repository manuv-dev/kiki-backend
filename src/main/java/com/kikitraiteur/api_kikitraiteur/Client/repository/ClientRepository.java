package com.kikitraiteur.api_kikitraiteur.Client.repository;

import com.kikitraiteur.api_kikitraiteur.Client.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findFirstByEmail(String email);
    Optional<Client> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<Client> findByEmailAndTelephone(String email, String telephone);
    Optional<Client> findByEmailAndPhone(String email, String phone);

    @Query("SELECT c FROM Client c WHERE c.email = :email AND (c.telephone = :phone OR c.phone = :phone)")
    List<Client> findAllByEmailAndPhoneOrTelephone(@Param("email") String email, @Param("phone") String phone);
}
