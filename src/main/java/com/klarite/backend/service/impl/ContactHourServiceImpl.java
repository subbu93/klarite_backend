package com.klarite.backend.service.impl;

import com.klarite.backend.Constants;
import com.klarite.backend.dto.ContinuedEducation;
import com.klarite.backend.dto.ContinuedEducationEvents;
import com.klarite.backend.dto.Training;
import com.klarite.backend.service.ContactHourService;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class ContactHourServiceImpl implements ContactHourService {

    @Override
    public ContinuedEducationEvents getAll(Long userId, JdbcTemplate jdbcTemplate) {
        ContinuedEducationEvents ce = new ContinuedEducationEvents();
        ce.setCeHrsPerYear(100); // todo:ishan- fix this value
        ce.setTrainings(getTrainingsWithCE(userId, jdbcTemplate));
        ce.setEducation(getEducationWithCE(userId, jdbcTemplate));
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
        obj.setDate((Date) row.get("date"));
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
            ce.setUrl(getTempFileName(ce));
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

    private List<ContinuedEducation> getEducationWithCE(Long userId, JdbcTemplate jdbcTemplate) {
        List<ContinuedEducation> educations = new ArrayList<>();
        String query = "SELECT * FROM " + Constants.TABLE_CONTACT_HOURS + " WHERE is_ce = 1 AND user_id = ?;";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, userId);
        for (Map<String, Object> row : rows) {
            ContinuedEducation obj = new ContinuedEducation();

            obj.setId((Long) row.get("id"));
            obj.setUserId((Long) row.get("user_id"));
            obj.setName((String) row.get("name"));
            obj.setDate((Date) row.get("date"));
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

    private String getTempFileName(ContinuedEducation ce) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.getTime() + ".jpg";
    }
}
