package com.klarite.backend.service.impl;

import com.klarite.backend.Constants;
import com.klarite.backend.dto.*;
import com.klarite.backend.service.AdminService;
import com.klarite.backend.service.ContactHourService;
import com.klarite.backend.service.UserService;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class ContactHourServiceImpl implements ContactHourService {
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;

    @Override
    public ContinuedEducationEvents getAll(Long userId, JdbcTemplate jdbcTemplate) {
        ContinuedEducationEvents ce = new ContinuedEducationEvents();
        ce.setCeHrsPerYear(getThreshold(userId, jdbcTemplate));
        ce.setTrainings(getTrainingsWithCE(userId, jdbcTemplate));
        ce.setEducation(getEducation(userId, jdbcTemplate));
        return ce;
    }

    @Override
    public ContinuedEducation get(Long id, JdbcTemplate jdbcTemplate) {
        String query = "SELECT * FROM " + Constants.TABLE_CONTACT_HOURS + " WHERE id = ?;";
        Map<String, Object> row = jdbcTemplate.queryForMap(query, id);
        
        ContinuedEducation obj = new ContinuedEducation();
        obj.setId((Long) row.get("id"));
        obj.setUserId((Long) row.get("user_id"));
        obj.setName((String) row.get("name"));
        java.sql.Date date = (java.sql.Date) row.get("date");
        if (date != null)
            obj.setDate(date.toLocalDate());
        obj.setCE((Boolean) row.get("is_ce"));
        obj.setPresenterName((String) row.get("presenter"));
        obj.setTotalHours((Float) row.get("total_hours"));
        obj.setDescription((String) row.get("description"));
        obj.setUrl((String) row.get("url"));
        if (obj.getUrl() != null && obj.getUrl().length() > 0) {
            try {
                String imagePath = Paths.get(Constants.DIRECTORY_DOCUMENTS, obj.getUrl()).toAbsolutePath().toString();
                byte[] fileContent = FileUtils.readFileToByteArray(new File(imagePath));
                obj.setImageData(Base64.getEncoder().encodeToString(fileContent));
            } catch (Exception e) {
                //todo: report error
            } 
        }

        return obj;
    }    

    @Override
    public ResponseEntity<Object> add(ContinuedEducation ce, JdbcTemplate jdbcTemplate) {
        try {
            String query = "INSERT INTO" + Constants.TABLE_CONTACT_HOURS + "VALUES(?, ?, ?, ?, ?, ?, ?, ?);";
            ce.setUrl(getTempFileName());
            updateFileOnDisk(ce);
            jdbcTemplate.update(query, ce.getUserId(), ce.getName(), ce.getDate(), ce.isCE(), ce.getPresenterName(),
                    ce.getTotalHours(), ce.getDescription(), ce.getUrl());
            ResponseEntity<Object> response = new ResponseEntity<>(Constants.MSG_UPDATED_SUCCESSFULLY, HttpStatus.CREATED);
            return response;
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Object> edit(ContinuedEducation ce, JdbcTemplate jdbcTemplate) {
        try {
            String query = "UPDATE" + Constants.TABLE_CONTACT_HOURS + "SET name = ?, date = ?,"
                    + " is_ce = ?, presenter = ?, total_hours = ?, description = ?, url = ? WHERE id = ?;";
            
            updateFileOnDisk(ce);
            if (ce.getImageData() == null || ce.getImageData().length() == 0) {
                ce.setUrl(null);
            }
            
            jdbcTemplate.update(query, ce.getName(), ce.getDate(), ce.isCE(), ce.getPresenterName(), ce.getTotalHours(),
                    ce.getDescription(), ce.getUrl(), ce.getId());
            ResponseEntity<Object> response = new ResponseEntity<>(Constants.MSG_UPDATED_SUCCESSFULLY, HttpStatus.CREATED);
            return response;
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<Certification> getAllCertifications(JdbcTemplate jdbcTemplate) {
        String certificationQuery = "SELECT * FROM " + Constants.TABLE_CERTIFICATIONS;
        List<Certification> certifications = new ArrayList<>();
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(certificationQuery);
            for (Map<String, Object> row: rows) {
                Certification certification = new Certification();

                certification.setId((Integer) row.get("id"));
                certification.setName((String) row.get("name"));
                certification.setDescription((String) row.get("description"));

                certifications.add(certification);
            }

            return certifications;
        } catch (Exception e) {
            return certifications;
        }
    }

    @Override
    public List<CeReport> getCeReport(Integer businessUnitId, Integer costCenterId, JdbcTemplate jdbcTemplate) {
        UserServiceImpl userService = new UserServiceImpl();

        List<User> users = userService.getUsersWithBusinessUnitIdAndCostCenterId(businessUnitId,
                costCenterId, jdbcTemplate);
        List<CeReport> ceReport = new ArrayList<>();
        for(User user: users) {
            CeReport temp = new CeReport();
            ContinuedEducationEvents continuedEducationEvents = getAll(user.getId(), jdbcTemplate);
            float totalHrs = 0;
            System.out.println(continuedEducationEvents);
            if(continuedEducationEvents.getEducation().size() > 0 ||
                    continuedEducationEvents.getTrainings().size() > 0) {
                if(continuedEducationEvents.getEducation().size() > 0) {
                    for (ContinuedEducation e: continuedEducationEvents.getEducation()) {
                        totalHrs += e.getTotalHours();
                    }
                }
                if(continuedEducationEvents.getTrainings().size() > 0) {
                    for (Training t: continuedEducationEvents.getTrainings()) {
                        totalHrs += t.getTotalHours();
                    }
                }
            }
            temp.setUser(user);
            temp.setTotalHrs(totalHrs);
            ceReport.add(temp);
        }
        return ceReport;
    }

    private List<Training> getTrainingsWithCE(Long userId, JdbcTemplate jdbcTemplate) {
        List<Training> trainings = new ArrayList<>();
        String query = "SELECT * FROM " + Constants.TABLE_TRAININGS
                + " WHERE is_ce = 1 AND soft_delete = 0 AND id IN (SELECT training_id FROM "
                + Constants.TABLE_T_ASSIGNMENTS + " WHERE id IN (SELECT assignment_id FROM "
                + Constants.TABLE_TRAINING_ASSIGNMENTS + " WHERE user_id = ? and attended = 1));";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, userId);
        for (Map<String, Object> row : rows) {
            Training obj = new Training();

            obj.setTrainingName((String) row.get("name"));
            obj.setDescription((String) row.get("description"));
            obj.setCE((Boolean) row.get("is_ce"));
            obj.setCeId((Long) row.get("ce_id"));
            obj.setTrainerId((Long) row.get("trainer_id"));
            obj.setTotalHours((Integer) row.get("total_hours"));

            trainings.add(obj);
        }

        return trainings;
    }

    private List<ContinuedEducation> getEducation(Long userId, JdbcTemplate jdbcTemplate) {
        List<ContinuedEducation> educations = new ArrayList<>();
        String query = "SELECT * FROM " + Constants.TABLE_CONTACT_HOURS + " WHERE user_id = ?;";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, userId);
        for (Map<String, Object> row : rows) {
            ContinuedEducation obj = new ContinuedEducation();

            obj.setId((Long) row.get("id"));
            obj.setUserId((Long) row.get("user_id"));
            obj.setName((String) row.get("name"));
            java.sql.Date date = (java.sql.Date) row.get("date");
            if (date != null)
                obj.setDate(date.toLocalDate());
            obj.setCE((Boolean) row.get("is_ce"));
            obj.setPresenterName((String) row.get("presenter"));
            obj.setTotalHours((Float) row.get("total_hours"));
            obj.setDescription((String) row.get("description"));
            obj.setUrl((String) row.get("description"));
            educations.add(obj);
        }

        return educations;
    }

    private void updateFileOnDisk(ContinuedEducation ce) throws FileNotFoundException, IOException {
        String imagePath = Paths.get(Constants.DIRECTORY_DOCUMENTS, ce.getUrl()).toAbsolutePath().toString();
        if (ce.getImageData() != null && ce.getImageData().length() != 0) {
            byte[] imageByte=Base64.getDecoder().decode(ce.getImageData());
            FileOutputStream fs = new FileOutputStream(imagePath, false);
            fs.write(imageByte);
            fs.close();
        } else {
            new File(imagePath).delete();
        }
    }

    private String getTempFileName() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.getTime() + ".jpg";
    }

    private Integer getThreshold(Long userId, JdbcTemplate jdbcTemplate) {
        User usr = userService.getUser(userId, false, jdbcTemplate);
        if (usr.getLicenseList().size() > 0) {
            ContactHours contactHours = adminService.getCeHrs("Ohio", usr.getLicenseList().get(0).getId(), jdbcTemplate);
            if (contactHours == null)
                return 0;
            else
                return contactHours.getCeHrs();
        } else 
            return 0;
    }
}
