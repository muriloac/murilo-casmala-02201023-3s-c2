package com.continuada2.tpw.repositorio;

import com.continuada2.tpw.dominio.Lutador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LutadorRepository extends JpaRepository<Lutador, Integer> {

    @Query("select l from Lutador l order by l.forcaGolpe desc")
    List<Lutador> findAllOrder();

    @Query("select count(l) from Lutador l where l.vida>0.0")
    Integer findAllAlive();

    @Query("select l from Lutador l where l.vida=0")
    List<Lutador> findAllDead();
}
