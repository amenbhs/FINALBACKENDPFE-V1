package com.example.PfeBack.Services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public void generateSampleNotifications() {
        // Clear existing notifications
        notificationRepository.deleteAll();
        
        // Generate basic sample notifications first
        generateBasicSampleNotifications();
        
        // Try to generate data-based notifications, but don't fail if no data exists
        try {
            generateAnimalNotifications();
        } catch (Exception e) {
            System.out.println("No animal data found, skipping animal notifications");
        }
        
        try {
            generatePlantNotifications();
        } catch (Exception e) {
            System.out.println("No plant data found, skipping plant notifications");
        }
        
        // Generate weather notifications
        generateWeatherNotifications();
        
        // Generate medical process notifications
        generateMedicalNotifications();
    }
    
    private void generateBasicSampleNotifications() {
        // Generate some basic notifications that don't depend on existing data
        createNotification(
            "System Startup",
            "Farm monitoring system has been started successfully. All sensors are online.",
            "system"
        );
        
        createNotification(
            "Daily Reminder",
            "Don't forget to check water levels in all tanks and ensure proper ventilation.",
            "reminder"
        );
        
        createNotification(
            "Maintenance Due",
            "Monthly equipment maintenance is due. Please check pumps, filters, and sensors.",
            "maintenance"
        );
        
        createNotification(
            "Weather Update",
            "Partly cloudy conditions expected today. Good day for outdoor activities with animals.",
            "weather"
        );
        
        createNotification(
            "Feed Schedule",
            "Morning feeding completed. Next feeding scheduled for 6:00 PM.",
            "feeding"
        );
        
        createNotification(
            "Health Check",
            "Weekly health inspection reminder: Check all animals for signs of illness or injury.",
            "health"
        );
    }

    public void generateDynamicNotifications() {
        // Generate notifications based on real data, but include basic ones if no data exists
        try {
            generateAnimalNotifications();
        } catch (Exception e) {
            System.out.println("No animal data found for dynamic notifications");
        }
        
        try {
            generatePlantNotifications();
        } catch (Exception e) {
            System.out.println("No plant data found for dynamic notifications");
        }
        
        generateWeatherNotifications();
        generateMedicalNotifications();
    }

    private void generateAnimalNotifications() {
        List<Animal> animals = animalRepository.findAll();
        
        for (Animal animal : animals) {
            // Check water intake
            if (animal.getTodayIntakeLiters() != null && animal.getRecommendedIntakeLiters() != null) {
                double intakeRatio = animal.getTodayIntakeLiters() / animal.getRecommendedIntakeLiters();
                if (intakeRatio < 0.6) { // Less than 60% of recommended intake
                    createNotification(
                        "Low Water Intake Alert",
                        String.format("Your %s '%s' has only consumed %.1fL of water today. Recommended intake is %.1fL. Check water tank and animal health.",
                            animal.getSpecies(), animal.getName(), animal.getTodayIntakeLiters(), animal.getRecommendedIntakeLiters()),
                        "animal"
                    );
                }
            }
            
            // Check health status
            if (animal.getHealthStatus() != null && !animal.getHealthStatus().equalsIgnoreCase("healthy")) {
                createNotification(
                    "Health Status Alert",
                    String.format("Your %s '%s' has health status: %s. Consider veterinary examination.",
                        animal.getSpecies(), animal.getName(), animal.getHealthStatus()),
                    "animal"
                );
            }
            
            // Check vaccination dates
            if (animal.getVaccinationDate() != null) {
                try {
                    LocalDate vaccinationDate = LocalDate.parse(animal.getVaccinationDate());
                    LocalDate nextVaccination = vaccinationDate.plusMonths(6); // Assume 6-month vaccination cycle
                    long daysUntilVaccination = ChronoUnit.DAYS.between(LocalDate.now(), nextVaccination);
                    
                    if (daysUntilVaccination <= 7 && daysUntilVaccination > 0) {
                        createNotification(
                            "Vaccination Due Soon",
                            String.format("Vaccination for your %s '%s' is due in %d days. Schedule appointment with veterinarian.",
                                animal.getSpecies(), animal.getName(), daysUntilVaccination),
                            "animal"
                        );
                    }
                } catch (Exception e) {
                    // Handle date parsing errors silently
                }
            }
            
            // Check next visit dates
            if (animal.getNextVisit() != null) {
                try {
                    LocalDate nextVisit = LocalDate.parse(animal.getNextVisit());
                    long daysUntilVisit = ChronoUnit.DAYS.between(LocalDate.now(), nextVisit);
                    
                    if (daysUntilVisit <= 1 && daysUntilVisit >= 0) {
                        createNotification(
                            "Vet Visit Reminder",
                            String.format("Veterinary visit for your %s '%s' is scheduled for %s.",
                                animal.getSpecies(), animal.getName(), animal.getNextVisit()),
                            "animal"
                        );
                    }
                } catch (Exception e) {
                    // Handle date parsing errors silently
                }
            }
            
            // Check medical processes
            if (animal.getMedicalProcesses() != null) {
                for (MedicalProcess process : animal.getMedicalProcesses()) {
                    if ("in_progress".equalsIgnoreCase(process.getStatus())) {
                        createNotification(
                            "Medical Treatment In Progress",
                            String.format("Ongoing medical treatment for %s '%s': %s. Monitor progress and follow treatment schedule.",
                                animal.getSpecies(), animal.getName(), process.getDiagnosis()),
                            "medical"
                        );
                    }
                }
            }
        }
    }

    private void generatePlantNotifications() {
        List<Plant> plants = plantRepository.findAll();
        
        for (Plant plant : plants) {
            // Check soil moisture
            if (plant.getSoilMoisture() != null && plant.getSoilMoisture() < 30) {
                createNotification(
                    "Low Soil Moisture Alert",
                    String.format("Your %s '%s' has soil moisture at %d%%. Irrigation recommended immediately.",
                        plant.getType(), plant.getName(), plant.getSoilMoisture()),
                    "plant"
                );
            }
            
            // Check harvest dates
            if (plant.getExpectedHarvestDate() != null) {
                try {
                    LocalDate harvestDate = LocalDate.parse(plant.getExpectedHarvestDate());
                    long daysUntilHarvest = ChronoUnit.DAYS.between(LocalDate.now(), harvestDate);
                    
                    if (daysUntilHarvest <= 7 && daysUntilHarvest >= 0) {
                        createNotification(
                            "Harvest Time Approaching",
                            String.format("Your %s '%s' is ready for harvest in %d days. Prepare harvesting equipment.",
                                plant.getType(), plant.getName(), daysUntilHarvest),
                            "plant"
                        );
                    } else if (daysUntilHarvest < 0) {
                        createNotification(
                            "Overdue Harvest",
                            String.format("Your %s '%s' harvest date has passed. Harvest immediately to prevent crop loss.",
                                plant.getType(), plant.getName()),
                            "plant"
                        );
                    }
                } catch (Exception e) {
                    // Handle date parsing errors silently
                }
            }
            
            // Check health status
            if (plant.getHealthStatus() != null && !plant.getHealthStatus().equalsIgnoreCase("healthy")) {
                createNotification(
                    "Plant Health Alert",
                    String.format("Your %s '%s' shows health issues: %s. Consider treatment or consultation.",
                        plant.getType(), plant.getName(), plant.getHealthStatus()),
                    "plant"
                );
            }
            
            // Check disease history
            if (plant.getDiseaseHistory() != null && !plant.getDiseaseHistory().trim().isEmpty()) {
                createNotification(
                    "Disease History Alert",
                    String.format("Your %s '%s' has disease history: %s. Monitor for recurring symptoms.",
                        plant.getType(), plant.getName(), plant.getDiseaseHistory()),
                    "plant"
                );
            }
            
            // Check next treatment dates
            if (plant.getNextTreatment() != null) {
                try {
                    LocalDate treatmentDate = LocalDate.parse(plant.getNextTreatment());
                    long daysUntilTreatment = ChronoUnit.DAYS.between(LocalDate.now(), treatmentDate);
                    
                    if (daysUntilTreatment <= 1 && daysUntilTreatment >= 0) {
                        createNotification(
                            "Treatment Due",
                            String.format("Treatment for your %s '%s' is due %s. Prepare necessary materials.",
                                plant.getType(), plant.getName(), 
                                daysUntilTreatment == 0 ? "today" : "tomorrow"),
                            "plant"
                        );
                    }
                } catch (Exception e) {
                    // Handle date parsing errors silently
                }
            }
            
            // Check watering schedule
            if (plant.getLastWatered() != null) {
                try {
                    LocalDateTime lastWatered = LocalDateTime.parse(plant.getLastWatered());
                    long hoursSinceWatering = ChronoUnit.HOURS.between(lastWatered, LocalDateTime.now());
                    
                    if (hoursSinceWatering > 72) { // More than 3 days
                        createNotification(
                            "Watering Overdue",
                            String.format("Your %s '%s' hasn't been watered for %d hours. Water immediately.",
                                plant.getType(), plant.getName(), hoursSinceWatering),
                            "plant"
                        );
                    }
                } catch (Exception e) {
                    // Handle date parsing errors silently
                }
            }
        }
    }

    private void generateWeatherNotifications() {
        // Frost warning
        createNotification(
            "Frost Warning", 
            "Temperature expected to drop to -2°C tonight. Protect sensitive plants and ensure animal shelter heating.",
            "weather"
        );
        
        // Heavy rain alert
        createNotification(
            "Heavy Rain Alert", 
            "Storm warning: 50mm rainfall expected in next 24 hours. Secure equipment and check drainage systems.",
            "weather"
        );
        
        // Drought warning
        createNotification(
            "Drought Conditions", 
            "No significant rainfall predicted for next 14 days. Implement water conservation measures.",
            "weather"
        );
        
        // Heat wave warning
        createNotification(
            "Heat Wave Alert", 
            "Temperatures exceeding 35°C expected for next 3 days. Ensure adequate shade and water for animals.",
            "weather"
        );
        
        // Wind warning
        createNotification(
            "Strong Wind Warning", 
            "Wind speeds up to 70 km/h expected. Secure loose structures and check tree stability.",
            "weather"
        );
    }

    private void generateMedicalNotifications() {
        // Treatment reminders
        createNotification(
            "Treatment Reminder", 
            "Antibiotic treatment for cow #247 due at 2:00 PM today. Day 3 of 7-day treatment cycle.",
            "medical"
        );
        
        // Follow-up appointments
        createNotification(
            "Vet Follow-up", 
            "Follow-up examination scheduled for injured horse 'Thunder' tomorrow at 10:00 AM.",
            "medical"
        );
        
        // Medical process completion
        createNotification(
            "Treatment Complete", 
            "7-day deworming treatment for sheep flock completed successfully. Next treatment due in 3 months.",
            "medical"
        );
        
        // Emergency medical alert
        createNotification(
            "Medical Emergency", 
            "Urgent: Cow showing signs of bloat. Contact veterinarian immediately.",
            "medical"
        );
        
        // Quarantine reminder
        createNotification(
            "Quarantine Update", 
            "Newly arrived cattle completing quarantine period in 2 days. Prepare integration plan.",
            "medical"
        );
    }

    private void createNotification(String title, String message, String type) {
        Notification notification = new Notification(title, message, type);
        // Randomize creation time for more realistic data
        LocalDateTime randomTime = LocalDateTime.now().minusHours(random.nextInt(72));
        notification.setCreatedAt(randomTime);
        notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        try {
            return notificationRepository.findAllByOrderByCreatedAtDesc();
        } catch (Exception e) {
            e.printStackTrace();
            return notificationRepository.findAll();
        }
    }

    public List<Notification> getNotificationsFiltered(String type, Boolean unreadOnly) {
        List<Notification> list;
        try {
            if (type != null && !type.isBlank()) {
                if (Boolean.TRUE.equals(unreadOnly)) {
                    list = notificationRepository.findByTypeAndReadFalse(type);
                } else {
                    list = notificationRepository.findByType(type);
                }
            } else {
                if (Boolean.TRUE.equals(unreadOnly)) {
                    list = notificationRepository.findByReadFalse();
                } else {
                    list = notificationRepository.findAll();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            list = notificationRepository.findAll();
        }
        // Sort by createdAt desc if available
        return list.stream()
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .collect(java.util.stream.Collectors.toList());
    }
    public void createTestNotification() {
    Notification notif = new Notification("Animal Health", "Cow needs vaccination", "animal");
    notificationRepository.save(notif);
}

    public List<Notification> getUnreadNotifications() {
        try {
            return notificationRepository.findByReadFalse();
        } catch (Exception e) {
            e.printStackTrace();
            return notificationRepository.findAll().stream()
                .filter(notification -> !notification.isRead())
                .collect(Collectors.toList());
        }
    }

    public Notification markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null) {
            notification.setRead(true);
            return notificationRepository.save(notification);
        }
        return null;
    }

    public void markAllAsRead() {
        List<Notification> notifications = notificationRepository.findByReadFalse();
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    // Farm-specific method for marking all notifications as read for a specific farm
    public void markAllAsRead(String farmId) {
        List<Notification> notifications = notificationRepository.findByFarmIdAndReadFalse(farmId);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    // Farm-specific method for generating dynamic notifications for a specific farm
    public void generateDynamicNotifications(String farmId) {
        // Generate notifications based on real data for a specific farm
        try {
            generateAnimalNotificationsForFarm(farmId);
        } catch (Exception e) {
            System.out.println("No animal data found for farm " + farmId);
        }
        
        try {
            generatePlantNotificationsForFarm(farmId);
        } catch (Exception e) {
            System.out.println("No plant data found for farm " + farmId);
        }
    }

    private void generateAnimalNotificationsForFarm(String farmId) {
        List<Animal> animals = animalRepository.findByFarmId(farmId);
        
        for (Animal animal : animals) {
            // Check water intake
            if (animal.getTodayIntakeLiters() != null && animal.getRecommendedIntakeLiters() != null) {
                double intakeRatio = animal.getTodayIntakeLiters() / animal.getRecommendedIntakeLiters();
                if (intakeRatio < 0.6) { // Less than 60% of recommended intake
                    createNotificationForFarm(
                        "Low Water Intake Alert",
                        String.format("Your %s '%s' has only consumed %.1fL of water today. Recommended intake is %.1fL. Check water tank and animal health.",
                            animal.getSpecies(), animal.getName(), animal.getTodayIntakeLiters(), animal.getRecommendedIntakeLiters()),
                        "animal",
                        farmId
                    );
                }
            }
            
            // Check health status
            if (animal.getHealthStatus() != null && !animal.getHealthStatus().equalsIgnoreCase("healthy")) {
                createNotificationForFarm(
                    "Health Status Alert",
                    String.format("Your %s '%s' has health status: %s. Consider veterinary examination.",
                        animal.getSpecies(), animal.getName(), animal.getHealthStatus()),
                    "animal",
                    farmId
                );
            }
        }
    }

    private void generatePlantNotificationsForFarm(String farmId) {
        List<Plant> plants = plantRepository.findByFarmId(farmId);
        
        for (Plant plant : plants) {
            // Check soil moisture
            if (plant.getSoilMoisture() != null && plant.getSoilMoisture() < 30) {
                createNotificationForFarm(
                    "Low Soil Moisture Alert",
                    String.format("Your %s '%s' has soil moisture at %d%%. Irrigation recommended immediately.",
                        plant.getType(), plant.getName(), plant.getSoilMoisture()),
                    "plant",
                    farmId
                );
            }
            
            // Check harvest dates
            if (plant.getExpectedHarvestDate() != null) {
                try {
                    LocalDate harvestDate = LocalDate.parse(plant.getExpectedHarvestDate());
                    long daysUntilHarvest = ChronoUnit.DAYS.between(LocalDate.now(), harvestDate);
                    
                    if (daysUntilHarvest <= 7 && daysUntilHarvest >= 0) {
                        createNotificationForFarm(
                            "Harvest Time Approaching",
                            String.format("Your %s '%s' is ready for harvest in %d days. Prepare harvesting equipment.",
                                plant.getType(), plant.getName(), daysUntilHarvest),
                            "plant",
                            farmId
                        );
                    }
                } catch (Exception e) {
                    // Handle date parsing errors silently
                }
            }
            
            // Check health status
            if (plant.getHealthStatus() != null && !plant.getHealthStatus().equalsIgnoreCase("healthy")) {
                createNotificationForFarm(
                    "Plant Health Alert",
                    String.format("Your %s '%s' shows health issues: %s. Consider treatment or consultation.",
                        plant.getType(), plant.getName(), plant.getHealthStatus()),
                    "plant",
                    farmId
                );
            }
        }
    }

    private void createNotificationForFarm(String title, String message, String type, String farmId) {
        Notification notification = new Notification(title, message, type, farmId);
        // Randomize creation time for more realistic data
        LocalDateTime randomTime = LocalDateTime.now().minusHours(random.nextInt(72));
        notification.setCreatedAt(randomTime);
        notificationRepository.save(notification);
    }
}
