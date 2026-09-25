package com.telusko.Smart_Job_Application_Automation.Service;

import com.telusko.Smart_Job_Application_Automation.Model.Recruiter;
import com.telusko.Smart_Job_Application_Automation.Repository.RecruiterRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecruiterService {

    private final RecruiterRepository recruiterRepository;

    public RecruiterService(RecruiterRepository recruiterRepository) {
        this.recruiterRepository = recruiterRepository;
    }


    public List<Recruiter> getRecruitersByIds(List<Long> ids) {
        return recruiterRepository.findAllById(ids);
    }
    public Recruiter saveRecruiter(Recruiter recruiter) {
        return recruiterRepository.save(recruiter);
    }


    public List<Recruiter> saveRecruiters(List<Recruiter> recruiters) {

        List<Recruiter> savedRecruiters = new ArrayList<>();

        for (Recruiter recruiter : recruiters) {

            if (recruiter.getEmail() == null ||
                    recruiter.getEmail().isBlank()) {
                continue;
            }

            String normalizedEmail =
                    recruiter.getEmail()
                            .trim()
                            .toLowerCase();

            recruiter.setEmail(normalizedEmail);


            Optional<Recruiter> existingRecruiter =
                    recruiterRepository.findByEmail(
                            normalizedEmail
                    );


            if (existingRecruiter.isPresent()) {

                Recruiter existing =
                        existingRecruiter.get();


                if (!existing.isActive()) {

                    existing.setActive(true);

                    existing.setFirstName(
                            recruiter.getFirstName()
                    );

                    existing.setCompany(
                            recruiter.getCompany()
                    );

                    existing.setPosition(
                            recruiter.getPosition()
                    );

                    existing.setStatus(
                            "PENDING"
                    );

                    existing.setSentTime(
                            null
                    );


                    Recruiter reactivated =
                            recruiterRepository.save(
                                    existing
                            );


                    savedRecruiters.add(
                            reactivated
                    );

                    continue;
                }
                System.out.println(
                        "Duplicate recruiter skipped: "
                                + normalizedEmail
                );
                continue;
            }
            recruiter.setActive(true);
            Recruiter savedRecruiter =
                    recruiterRepository.save(
                            recruiter
                    );
            savedRecruiters.add(
                    savedRecruiter
            );
        }
        return savedRecruiters;
    }


    public long getTotalRecruiters() {
        return recruiterRepository.countByActiveTrue();
    }

    public long getPendingCount() {
        return recruiterRepository.countByStatusAndActiveTrue("PENDING");
    }

    public long getSentCount() {
        return recruiterRepository.countByStatusAndActiveTrue("SENT");
    }

    public long getFailedCount() {
        return recruiterRepository.countByStatusAndActiveTrue("FAILED");
    }


    public List<Recruiter> getAllRecruiters() {
        return recruiterRepository.findByActiveTrue();
    }

    public List<Recruiter> getRecentEmailActivity() {

        return recruiterRepository
                .findTop5ByStatusInOrderByIdDesc(
                        List.of("SENT", "FAILED")
                );
    }


    public void deleteRecruiter(Long id) {

        Recruiter recruiter =
                recruiterRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Recruiter not found with id: " + id
                                )
                        );

        recruiter.setActive(false);
        recruiterRepository.save(recruiter);
    }



    public long countActiveRecruiters() {
        return recruiterRepository.countByActiveTrue();
    }
    public long countSentRecruiters() {
        return recruiterRepository
                .countByStatusAndActiveTrue("SENT");
    }
    public long countFailedRecruiters() {
        return recruiterRepository
                .countByStatusAndActiveTrue("FAILED");
    }
    public long countPendingRecruiters() {
        return recruiterRepository
                .countByStatusAndActiveTrue("PENDING");
    }
}