package com.continuada2.tpw.controle;

import com.continuada2.tpw.dominio.Golpe;
import com.continuada2.tpw.dominio.Lutador;
import com.continuada2.tpw.repositorio.LutadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class LutadorController {

    @Autowired
    private LutadorRepository repository;

    @PostMapping("/lutadores")
    public ResponseEntity postLutador(@RequestBody @Valid Lutador novoLutador) {
        repository.save(novoLutador);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/lutadores")
    public ResponseEntity getLutadores() {
        return ResponseEntity.status(200).body(repository.findAllOrder());
    }

    @GetMapping("/lutadores/contagem-vivos")
    public ResponseEntity getLutadoresVivos() {
        return ResponseEntity.status(200).body(repository.findAllAlive());
    }

    @PostMapping("/lutadores/{id}/concentrar")
    public ResponseEntity concentrar(@PathVariable Integer id) {
        Optional<Lutador> lutador = repository.findById(id);

        if (lutador.isPresent()) {
            if (lutador.get().getConcentracoesRealizadas() < 3) {
                lutador.get().setConcentracoesRealizadas(lutador.get().getConcentracoesRealizadas() + 1);
                lutador.get().setVida(lutador.get().getVida() * 1.15);
                repository.save(lutador.get());
                return ResponseEntity.status(200).build();
            } else {
                return ResponseEntity.status(400).body("Lutador já se concentrou 3 vezes!");
            }
        } else {
            return ResponseEntity.of(lutador);
        }
    }

    @PostMapping("/lutadores/golpe")
    public ResponseEntity golpe(@RequestBody Golpe payload) {

        if (payload.getIdLutadorApanha() > 0 && payload.getIdLutadorBate() > 0) {
            Optional<Lutador> lutadorBate = repository.findById(payload.getIdLutadorBate());
            Optional<Lutador> lutadorApanha = repository.findById(payload.getIdLutadorApanha());
            if (lutadorBate.isPresent() && lutadorApanha.isPresent()) {
                if (lutadorApanha.get().getVivo() && lutadorBate.get().getVivo()) {
                    Double forca = lutadorBate.get().getForcaGolpe();
                    Double vida = lutadorApanha.get().getVida();
                    double vidaFinal = vida - forca;

                    if (vidaFinal <= 0) {
                        vidaFinal = 0.0;
                        lutadorApanha.get().setVivo(false);
                    }

                    lutadorApanha.get().setVida(vidaFinal);
                    repository.save(lutadorApanha.get());

                    ArrayList<Optional<Lutador>> lutadores = new ArrayList();

                    lutadores.add(lutadorBate);
                    lutadores.add(lutadorApanha);


                    return ResponseEntity.status(201).body(lutadores);

                } else {
                    return ResponseEntity.status(404).body("Ambos os lutadores devem estar vivos");
                }
            } else {
                return ResponseEntity.status(404).body("");
            }


        } else {
            return ResponseEntity.status(404).body("Os ID's devem ser maiores do que 0");
        }
    }

    @GetMapping("/lutadores/mortos")
    public ResponseEntity mortos() {
        return ResponseEntity.status(200).body(repository.findAllDead());
    }
}
