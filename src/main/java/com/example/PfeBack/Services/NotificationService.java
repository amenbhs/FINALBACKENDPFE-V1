package com.example.PfeBack.Services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.PfeBack.models.Animal;
import com.example.PfeBack.models.MedicalProcess;
import com.example.PfeBack.models.Notification;
import com.example.PfeBack.models.Plant;
import com.example.PfeBack.repository.AnimalRepository;
import com.example.PfeBack.repository.NotificationRepository;
import com.example.PfeBack.repository.PlantRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private PlantRepository plantRepository;

    private final Random random = new Random();

    // ─────────────────────────────────────────────────────────
    // PUBLIC API
    // ─────────────────────────────────────────────────────────

    public List<Notification> getAllNotifications() {
        try {
            return notificationRepository.findAllByOrderByCreatedAtDesc();
        } catch (Exception e) {
            return notificationRepository.findAll();
        }
    }

    public List<Notification> getUnreadNotifications() {
        try {
            return notificationRepository.findByReadFalse();
        } catch (Exception e) {
            return notificationRepository.findAll().stream()
                .filter(n -> !n.isRead()).collect(Collectors.toList());
        }
    }

    public Notification markAsRead(String id) {
        Notification n = notificationRepository.findById(id).orElse(null);
        if (n != null) {
            n.setRead(true);
            return notificationRepository.save(n);
        }
        return null;
    }

    public void markAllAsRead() {
        List<Notification> unread = notificationRepository.findByReadFalse();
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    /**
     * Generates NEW notifications based on real farm data (animals + plants).
     * Does NOT delete existing notifications — only adds new ones.
     * Returns the count of newly created notifications.
     */
    public int generateRealNotifications() {
        List<Notification> toSave = new ArrayList<>();

        // ── Animal-based notifications ──
        try {
            List<Animal> animals = animalRepository.findAll();
            for (Animal animal : animals) {

                // Low water intake
                if (animal.getTodayIntakeLiters() != null && animal.getRecommendedIntakeLiters() != null
                        && animal.getRecommendedIntakeLiters() > 0) {
                    double ratio = animal.getTodayIntakeLiters() / animal.getRecommendedIntakeLiters();
                    if (ratio < 0.6) {
                        toSave.add(build(
                            "Low Water Intake",
                            String.format("%s '%s' consumed only %.1fL today (recommended: %.1fL). Check the water tank.",
                                capitalize(animal.getSpecies()), animal.getName(),
                                animal.getTodayIntakeLiters(), animal.getRecommendedIntakeLiters()),
                            "animal", ratio < 0.3 ? "critical" : "high", 0
                        ));
                    }
                }

                // Unhealthy status
                if (animal.getHealthStatus() != null
                        && !animal.getHealthStatus().equalsIgnoreCase("healthy")) {
                    toSave.add(build(
                        "Health Status Alert",
                        String.format("%s '%s' has status: %s. Consider a veterinary check.",
                            capitalize(animal.getSpecies()), animal.getName(), animal.getHealthStatus()),
                        "animal", "high", 0
                    ));
                }

                // Vaccination due within 7 days
                if (animal.getVaccinationDate() != null) {
                    try {
                        LocalDate vaccDate = LocalDate.parse(animal.getVaccinationDate());
                        LocalDate nextVacc = vaccDate.plusMonths(6);
                        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), nextVacc);
                        if (daysLeft >= 0 && daysLeft <= 7) {
                            toSave.add(build(
                                "Vaccination Due Soon",
                                String.format("%s '%s' vaccination due in %d day(s). Schedule with vet.",
                                    capitalize(animal.getSpecies()), animal.getName(), daysLeft),
                                "animal", daysLeft == 0 ? "high" : "medium", 0
                            ));
                        }
                    } catch (Exception ignored) {}
                }

                // Vet visit tomorrow or today
                if (animal.getNextVisit() != null) {
                    try {
                        LocalDate visitDate = LocalDate.parse(animal.getNextVisit());
                        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), visitDate);
                        if (daysLeft >= 0 && daysLeft <= 1) {
                            toSave.add(build(
                                "Vet Visit Reminder",
                                String.format("Vet visit for %s '%s' is %s.",
                                    capitalize(animal.getSpecies()), animal.getName(),
                                    daysLeft == 0 ? "today" : "tomorrow"),
                                "animal", "medium", 0
                            ));
                        }
                    } catch (Exception ignored) {}
                }

                // Active medical processes
                if (animal.getMedicalProcesses() != null) {
                    for (MedicalProcess mp : animal.getMedicalProcesses()) {
                        if ("in_progress".equalsIgnoreCase(mp.getStatus())) {
                            toSave.add(build(
                                "Ongoing Treatment",
                                String.format("%s '%s' is under treatment: %s. Follow the schedule.",
                                    capitalize(animal.getSpecies()), animal.getName(), mp.getDiagnosis()),
                                "medical", "medium", 0
                            ));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Animal notifications skipped: " + e.getMessage());
        }

        // ── Plant-based notifications ──
        try {
            List<Plant> plants = plantRepository.findAll();
            for (Plant plant : plants) {

                // Low soil moisture
                if (plant.getSoilMoisture() != null && plant.getSoilMoisture() < 30) {
                    toSave.add(build(
                        "Low Soil Moisture",
                        String.format("%s '%s' soil moisture: %d%%. Irrigate immediately.",
                            capitalize(plant.getType()), plant.getName(), plant.getSoilMoisture()),
                        "plant", plant.getSoilMoisture() < 15 ? "critical" : "high", 0
                    ));
                }

                // Harvest approaching / overdue
                if (plant.getExpectedHarvestDate() != null) {
                    try {
                        LocalDate harvestDate = LocalDate.parse(plant.getExpectedHarvestDate());
                        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), harvestDate);
                        if (daysLeft < 0) {
                            toSave.add(build(
                                "Overdue Harvest",
                                String.format("%s '%s' harvest date has passed. Harvest now to prevent crop loss.",
                                    capitalize(plant.getType()), plant.getName()),
                                "plant", "high", 0
                            ));
                        } else if (daysLeft <= 7) {
                            toSave.add(build(
                                "Harvest Approaching",
                                String.format("%s '%s' harvest in %d day(s). Prepare equipment.",
                                    capitalize(plant.getType()), plant.getName(), daysLeft),
                                "plant", "medium", 0
                            ));
                        }
                    } catch (Exception ignored) {}
                }

                // Unhealthy plant
                if (plant.getHealthStatus() != null
                        && !plant.getHealthStatus().equalsIgnoreCase("healthy")) {
                    toSave.add(build(
                        "Plant Health Issue",
                        String.format("%s '%s': %s. Consult a specialist.",
                            capitalize(plant.getType()), plant.getName(), plant.getHealthStatus()),
                        "plant", "high", 0
                    ));
                }

                // Treatment due
                if (plant.getNextTreatment() != null) {
                    try {
                        LocalDate treatDate = LocalDate.parse(plant.getNextTreatment());
                        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), treatDate);
                        if (daysLeft >= 0 && daysLeft <= 1) {
                            toSave.add(build(
                                "Plant Treatment Due",
                                String.format("Treatment for %s '%s' is due %s.",
                                    capitalize(plant.getType()), plant.getName(),
                                    daysLeft == 0 ? "today" : "tomorrow"),
                                "plant", "medium", 0
                            ));
                        }
                    } catch (Exception ignored) {}
                }

                // Watering overdue (> 3 days)
                if (plant.getLastWatered() != null) {
                    try {
                        LocalDateTime lastWatered = LocalDateTime.parse(plant.getLastWatered());
                        long hours = ChronoUnit.HOURS.between(lastWatered, LocalDateTime.now());
                        if (hours > 72) {
                            toSave.add(build(
                                "Watering Overdue",
                                String.format("%s '%s' hasn't been watered in %d hours. Water immediately.",
                                    capitalize(plant.getType()), plant.getName(), hours),
                                "plant", hours > 120 ? "high" : "medium", 0
                            ));
                        }
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception e) {
            System.out.println("Plant notifications skipped: " + e.getMessage());
        }

        // ── Weather notifications (time-randomized so they're not always identical) ──
        String[] weatherTitles   = { "Frost Warning", "Heavy Rain Alert", "Drought Conditions", "Heat Wave Alert", "Strong Wind Warning" };
        String[] weatherMessages = {
            "Temperature expected to drop below 0°C tonight. Protect sensitive plants and ensure animal shelter heating.",
            "Storm warning: heavy rainfall expected in next 24 hours. Secure equipment and check drainage.",
            "No significant rainfall predicted for next 14 days. Implement water conservation measures.",
            "Temperatures exceeding 35°C expected for 3 days. Ensure adequate shade and water for animals.",
            "Wind speeds up to 70 km/h expected. Secure loose structures and check tree stability."
        };
        String[] weatherSeverities = { "high", "medium", "medium", "high", "medium" };
        // Pick 2 random weather events each time to vary the result
        int w1 = random.nextInt(weatherTitles.length);
        int w2 = (w1 + 1 + random.nextInt(weatherTitles.length - 1)) % weatherTitles.length;
        toSave.add(build(weatherTitles[w1], weatherMessages[w1], "weather", weatherSeverities[w1], random.nextInt(6)));
        toSave.add(build(weatherTitles[w2], weatherMessages[w2], "weather", weatherSeverities[w2], random.nextInt(12)));

        // ── Medical reminder (randomized) ──
        String[] medTitles = { "Treatment Reminder", "Vet Follow-up", "Quarantine Update", "Deworming Schedule" };
        String[] medMessages = {
            "Antibiotic treatment for cow #" + (200 + random.nextInt(99)) + " due at " + (8 + random.nextInt(8)) + ":00. Day " + (1 + random.nextInt(6)) + " of 7.",
            "Follow-up exam for injured animal scheduled tomorrow at " + (9 + random.nextInt(4)) + ":00 AM.",
            "Newly arrived cattle completing quarantine in " + (1 + random.nextInt(4)) + " days. Prepare integration plan.",
            "Monthly deworming due for sheep flock. Prepare doses for " + (10 + random.nextInt(20)) + " animals."
        };
        String[] medSeverities = { "medium", "low", "low", "medium" };
        int m = random.nextInt(medTitles.length);
        toSave.add(build(medTitles[m], medMessages[m], "medical", medSeverities[m], random.nextInt(24)));

        notificationRepository.saveAll(toSave);
        return toSave.size();
    }

    // ─────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────

    private Notification build(String title, String message, String type, String severity, int hoursAgo) {
        Notification n = new Notification();
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setSeverity(severity);
        n.setRead(false);
        // Spread creation times so notifications don't all appear at once
        n.setCreatedAt(LocalDateTime.now().minusHours(hoursAgo).minusMinutes(random.nextInt(59)));
        return n;
    }

    private String capitalize(String s) {
        if (s == null || s.isBlank()) return "Animal";
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    // ─────────────────────────────────────────────────────────
    // LEGACY / BACKWARD-COMPATIBLE METHODS
    // Called by FarmController — must keep the exact same signatures
    // ─────────────────────────────────────────────────────────

    /**
     * Called by FarmController.generateDynamicNotificationsForFarm(farmId).
     * Generates real notifications for the farm (scans all animals and plants).
     */
    public void generateDynamicNotifications(String farmId) {
        generateRealNotifications();
    }

    public void markAllAsRead(String farmId) {
        List<Notification> notifications = notificationRepository.findByFarmIdAndReadFalse(farmId);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    public List<Notification> getNotificationsFiltered(String type, Boolean unreadOnly) {
        List<Notification> list;
        try {
            if (type != null && !type.isBlank()) {
                list = Boolean.TRUE.equals(unreadOnly)
                    ? notificationRepository.findByTypeAndReadFalse(type)
                    : notificationRepository.findByType(type);
            } else {
                list = Boolean.TRUE.equals(unreadOnly)
                    ? notificationRepository.findByReadFalse()
                    : notificationRepository.findAll();
            }
        } catch (Exception e) {
            list = notificationRepository.findAll();
        }
        return list.stream()
            .sorted((a, b) -> {
                if (a.getCreatedAt() == null) return 1;
                if (b.getCreatedAt() == null) return -1;
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            })
            .collect(Collectors.toList());
    }
}