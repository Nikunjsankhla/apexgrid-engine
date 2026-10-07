package com.apexgrid.engine.repository;

import com.apexgrid.engine.entity.MatchFixture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchFixtureRepository extends JpaRepository<MatchFixture, Long> {
    List<MatchFixture> findByTournamentIdOrderByRoundNumberAscMatchNumberInRoundAsc(Long tournamentId);
    Optional<MatchFixture> findByTournamentIdAndRoundNumberAndMatchNumberInRound(Long tournamentId, Integer roundNumber, Integer matchNumberInRound);
}
