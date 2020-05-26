package com.klarite.backend.service.impl;

import com.klarite.backend.Constants;
import com.klarite.backend.dto.Certification;
import com.klarite.backend.dto.Episode;
import com.klarite.backend.dto.License;
import com.klarite.backend.dto.Notification.ObservationRequestNotification;
import com.klarite.backend.dto.SkillEpisode;
import com.klarite.backend.dto.User;
import com.klarite.backend.service.NotificationService;
import com.klarite.backend.service.UserService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private NotificationService notificationService;

    @Override
    public ResponseEntity<Object> addSkillEpisode(Episode episode, JdbcTemplate jdbcTemplate) {
        try {
            String query = "INSERT INTO" + Constants.TABLE_EPISODES + "VALUES(?, ?, ?); SELECT SCOPE_IDENTITY() as id;";
            Map<String, Object> row = jdbcTemplate.queryForMap(query, episode.getUserId(), episode.getDate(),
                    episode.getMrn());
            BigDecimal episodeId = (BigDecimal) row.get("id");
            insertSkillEpisodes(episode.getUserId(), episodeId.longValue(), episode.getEpisodes(), jdbcTemplate);
            return new ResponseEntity<>("Stored", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Object> updateSkillEpisode(Episode episode, JdbcTemplate jdbcTemplate) {
        try {
            String query = "UPDATE" + Constants.TABLE_EPISODES +
                    "SET date = ?, mrn = ?, is_audited = ? WHERE id = ?;";
            jdbcTemplate.update(query, episode.getDate(), episode.getMrn(),
                    episode.isAudited(), episode.getId());
            insertSkillEpisodes(episode.getUserId(), episode.getId().longValue(),
                    episode.getEpisodes(), jdbcTemplate);
            return new ResponseEntity<>("Updated", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Object> markAttendance(String uuid, Long userId, JdbcTemplate jdbcTemplate) {
        try {
            Long trainingAssignmentId = isUUIDValid(uuid, jdbcTemplate);
            if (trainingAssignmentId != null) {
                if (isTimeValid(trainingAssignmentId, jdbcTemplate)) {
                    String query = "UPDATE " + Constants.TABLE_TRAINING_ASSIGNMENTS +
                            " SET attended = 1 WHERE uuid = ? and user_id = ?";
                    jdbcTemplate.update(query, trainingAssignmentId, userId);
                    return new ResponseEntity<>(Constants.MSG_MARK_ATTENDANCE_SUCCESS, HttpStatus.OK);
                } else
                    return new ResponseEntity<>(Constants.MSG_MARK_ATTENDANCE_INVALID_TIME, HttpStatus.BAD_REQUEST);
            } else
                return new ResponseEntity<>(Constants.MSG_MARK_ATTENDANCE_INVALID_UUID, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Object> updateProfilePic(Long userId, String imageData, JdbcTemplate jdbcTemplate) {
        try {
            String fileName = getUser(userId, false, jdbcTemplate).getUrl();
            if (fileName == null || fileName.length() == 0)
                fileName = getTempFileName();
            
            updateFileOnDisk(imageData, fileName);
            String query = "UPDATE " + Constants.TABLE_USERS + " SET image_url = ? WHERE id = ?";
            jdbcTemplate.update(query, fileName, userId);
            return new ResponseEntity<>(Constants.MSG_UPDATED_SUCCESSFULLY, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<User> getAllUsers(JdbcTemplate jdbcTemplate) {
        String query = "SELECT u.*, " +
                "       b.NAME as business_unit, " +
                "       c.NAME as cost_center " +
                " FROM   " + Constants.TABLE_USERS + " AS u, " +
                "       " + Constants.TABLE_COST_CENTER + " AS c, " +
                "       " + Constants.TABLE_BUSINESS_UNIT + " AS b " +
                " WHERE  u.cost_center_id = c.id " +
                "       AND u.business_unit_id = b.id " +
                "       AND u.soft_delete = 0;";
        List<User> users;
        try {
            users = new ArrayList<>();
            List<Map<String, Object>> userRows = jdbcTemplate.queryForList(query);
            for (Map<String, Object> row : userRows) {
                User user = new User();

                user.setId(((Long) row.get("id")));
                user.setOsuId((String) row.get("osu_id"));
                user.setFirstName((String) row.get("first_name"));
                user.setMiddleName((String) row.get("middle_name"));
                user.setLastName((String) row.get("last_name"));
                user.setEmail((String) row.get("email"));
                user.setBusinessUnitId((Integer) row.get("business_unit_id"));
                user.setCostCenterId((Integer) row.get("cost_center_id"));
                user.setBusinessUnitName((String) row.get("business_unit"));
                user.setCostCenterName((String) row.get("cost_center"));
                user.setUrl((String) row.get("image_url"));
                user.setRole((String) row.get("role"));
                user.setCertifications(getUserCertifications(user.getId(), jdbcTemplate));
                user.setTrainer((boolean) row.get("is_trainer"));
                user.setLicenseList(getUserLicenses(user.getId(), jdbcTemplate));
                users.add(user);
            }
            return users;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public ResponseEntity<Object> updateCertification(Long userId, List<Certification> certifications, JdbcTemplate jdbcTemplate) {
        try {
            deleteCurrentCertifications(userId, jdbcTemplate);
            String query = "INSERT INTO " + Constants.TABLE_USER_CERTIFICATION + "VALUES (? , ? , ?)";
            for (Certification certification : certifications) {
                jdbcTemplate.update(query, userId, certification.getId(), certification.getExpiry());
            }
            return new ResponseEntity<>(Constants.MSG_UPDATED_SUCCESSFULLY, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public User getUser(Long userId, Boolean getImageData, JdbcTemplate jdbcTemplate) {
        User usr = getUser(userId, jdbcTemplate);
        try {   
            String imagePath = Paths.get(Constants.DIRECTORY_PROFILE, usr.getUrl()).toAbsolutePath().toString();
            byte[] fileContent = FileUtils.readFileToByteArray(new File(imagePath));
            usr.setImageData(Base64.getEncoder().encodeToString(fileContent));     
        } catch (Exception e) {
            // todo: print error
        }
        return usr;
    }

    @Override
    public User getUser(Long userId, JdbcTemplate jdbcTemplate) {
        String query = "SELECT u.*, " +
                "       b.NAME as business_unit, " +
                "       c.NAME as cost_center" +
                " FROM   " + Constants.TABLE_USERS + " AS u, " +
                "       " + Constants.TABLE_COST_CENTER + " AS c, " +
                "       " + Constants.TABLE_BUSINESS_UNIT + " AS b " +
                " WHERE  u.cost_center_id = c.id " +
                "       AND u.business_unit_id = b.id " +
                "       AND u.id = ?" +
                "       AND u.soft_delete = 0";
        User user;
        try {
            Map<String, Object> row = jdbcTemplate.queryForMap(query, userId);

            user = new User();
            user.setId(((Long) row.get("id")));
            user.setOsuId((String) row.get("osu_id"));
            user.setFirstName((String) row.get("first_name"));
            user.setMiddleName((String) row.get("middle_name"));
            user.setLastName((String) row.get("last_name"));
            user.setEmail((String) row.get("email"));
            user.setBusinessUnitId((Integer) row.get("business_unit_id"));
            user.setCostCenterId((Integer) row.get("cost_center_id"));
            user.setBusinessUnitName((String) row.get("business_unit"));
            user.setCostCenterName((String) row.get("cost_center"));
            user.setUrl((String) row.get("image_url"));
            user.setTrainer((boolean) row.get("is_trainer"));
            user.setRole((String) row.get("role"));
            user.setCertifications(getUserCertifications(user.getId(), jdbcTemplate));
            user.setLicenseList(getUserLicenses(user.getId(), jdbcTemplate));
            user.setFirstLogin((Boolean) row.get("first_time_user"));

            return user;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ResponseEntity<Object> addUser(User user, JdbcTemplate jdbcTemplate) {
        if (user.getId() != null) {
            return updateUser(user, jdbcTemplate);
        } else {
            String insertUser = "INSERT INTO " + Constants.TABLE_USERS +
                    " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,0);  SELECT SCOPE_IDENTITY() as id;";
            try {
                Map<String, Object> row = jdbcTemplate.queryForMap(insertUser, user.getOsuId(), user.getFirstName(), user.getMiddleName(),
                        user.getLastName(), user.getEmail(), "5f4dcc3b5aa765d61d8327deb882cf99",
                        user.getCostCenterId(), user.getBusinessUnitId(), "./image8.jpg",
                        user.getRole(),user.isTrainer(), true);
                BigDecimal userId = (BigDecimal) row.get("id");
                insertUserLicenses(userId.longValue(), user.getLicenseList(), jdbcTemplate);
                return new ResponseEntity<>("Updated", HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Override
    public ResponseEntity<Object> deleteUser(Long userId, JdbcTemplate jdbcTemplate) {
        String query = "UPDATE " + Constants.TABLE_USERS +
                " SET soft_delete = 1 " +
                " WHERE id = ?";
        try {
            deleteUserLicense(userId, jdbcTemplate);
            jdbcTemplate.update(query, userId);
            return new ResponseEntity<>("Deleted User", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    public ResponseEntity<Object> changePswd(Long userId, String oldPswd, String newPswd, JdbcTemplate jdbcTemplate) {
        try {
            if (validateNewPswd(newPswd)) {
                String query = "SELECT * FROM" + Constants.TABLE_USERS + " WHERE id = ? and password = ?";
                jdbcTemplate.queryForMap(query, userId, oldPswd);
                query = "UPDATE " + Constants.TABLE_USERS + "SET password = ?, first_time_user = 0 WHERE id = ?";
                jdbcTemplate.update(query, newPswd, userId);
                return new ResponseEntity<>(Constants.MSG_UPDATED_SUCCESSFULLY, HttpStatus.CREATED);
            } else
                return new ResponseEntity<>(Constants.MSG_PSWD_RULE_VOILATED, HttpStatus.BAD_REQUEST);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(Constants.MSG_CURRENT_PSWD_INVLAID, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<Object> updateUser(User user, JdbcTemplate jdbcTemplate) {
        String updateUser = "UPDATE " + Constants.TABLE_USERS +
                " SET osu_id = ?" +
                "      ,first_name = ?" +
                "      ,middle_name = ?" +
                "      ,last_name = ?" +
                "      ,email = ?" +
                "      ,cost_center_id = ?" +
                "      ,business_unit_id = ?" +
                "      ,image_url = ?" +
                "      ,role = ?" +
                "      ,is_trainer  = ?" +
                " WHERE id = ?";
        try {
            jdbcTemplate.update(updateUser, user.getOsuId(), user.getFirstName(), user.getMiddleName(),
                    user.getLastName(), user.getEmail(), user.getCostCenterId(),
                    user.getBusinessUnitId(), "./image8.jpg", user.getRole(),
                    user.isTrainer(), user.getId());

            deleteUserLicense(user.getId(), jdbcTemplate);
            insertUserLicenses(user.getId(), user.getLicenseList(), jdbcTemplate);

            ResponseEntity<Object> response = new ResponseEntity<>("Updated", HttpStatus.CREATED);
            return response;
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private void insertSkillEpisodes(Long userId, long episodeId, List<SkillEpisode> skillEpisodeList,
                                     JdbcTemplate jdbcTemplate) throws DataAccessException {
        deleteExisitingEpisodes(episodeId, jdbcTemplate);

        String query = "INSERT INTO" + Constants.TABLE_SKILL_EPISODES + "VALUES(?, ?, ?, ?, ?, ?, ?);";
        ObservationRequestNotification orn = new ObservationRequestNotification();
        orn.setEpisodeId(episodeId);
        User usr = getUser(userId, jdbcTemplate);
        for (SkillEpisode skillEpisode : skillEpisodeList) {
            if (!skillEpisode.isObserved() || skillEpisode.getObserverId() == 0)
                jdbcTemplate.update(query, episodeId, skillEpisode.getSkillId(),
                        skillEpisode.isObserved(), null, 0, 0, null);
            else
                jdbcTemplate.update(query, episodeId, skillEpisode.getSkillId(),
                        skillEpisode.isObserved(), skillEpisode.getObserverId(), 0, 0, null);
            
            if (skillEpisode.isObserved()) {
                orn.addSkillId(skillEpisode.getSkillId());
            }
        }
        if (orn.getSkills() != null && orn.getSkills().size() > 0) {
            notificationService.add(orn, usr, null, jdbcTemplate);
        }
    }

    private void deleteExisitingEpisodes(long episodeId, JdbcTemplate jdbcTemplate) {
        String query = "DELETE FROM" + Constants.TABLE_SKILL_EPISODES + "where episode_id = ?;";
        jdbcTemplate.update(query, episodeId);
    }

    Long isUUIDValid(String uuid, JdbcTemplate jdbcTemplate) {
        String query = "SELECT id FROM " + Constants.TABLE_T_ASSIGNMENTS + " WHERE  uuid = ?";
        List<Map<String, Object>> row = jdbcTemplate.queryForList(query, uuid);
        return row.size() == 1 ? (Long) row.get(0).get("id") : null;
    }

    private boolean isTimeValid(Long trainingAssignmentId, JdbcTemplate jdbcTemplate) {
        String query = "SELECT uuid FROM " + Constants.TABLE_T_ASSIGNMENTS + " WHERE  id = ?)";
        Map<String, Object> row = jdbcTemplate.queryForMap(query, trainingAssignmentId);
        boolean isDateValid = ((Date) row.get("date")).equals(new Date(System.currentTimeMillis()));
        boolean isTimeValid = true;    //todo ishan: fix this
        return isDateValid && isTimeValid;
    }

    private String updateFileOnDisk(String imageData, String fileName) throws FileNotFoundException, IOException {
        String imagePath = Paths.get(Constants.DIRECTORY_PROFILE, fileName).toAbsolutePath().toString();
        if (imageData != null && imageData.length() != 0) {
            byte[] imageByte=Base64.getDecoder().decode(imageData);
            FileOutputStream fs = new FileOutputStream(imagePath, false);
            fs.write(imageByte);
            fs.close();
        } else {
            new File(imagePath).delete();
            fileName = "";
        }

        return fileName;
    }

    private String getTempFileName() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.getTime() + ".jpg";
    }

    private boolean validateNewPswd(String newPswd) {
        return newPswd.length() >= 6;
    }

    public List<User> getUsersWithBusinessUnitIdAndCostCenterId(Integer businessUnitId, Integer costCenterId,
                                                                JdbcTemplate jdbcTemplate) {
        String query = "SELECT u.*, " +
                "       b.NAME AS business_unit, " +
                "       c.NAME AS cost_center " +
                "FROM   "+Constants.TABLE_USERS+" AS u " +
                "       INNER JOIN "+ Constants.TABLE_COST_CENTER + " AS c " +
                "               ON u.cost_center_id = c.id " +
                "       INNER JOIN "+ Constants.TABLE_BUSINESS_UNIT + " AS b " +
                "               ON u.business_unit_id = b.id " +
                "WHERE  u.cost_center_id = ? " +
                "       AND u.business_unit_id = ? " +
                "       AND u.soft_delete = 0; ";

        List<User> users;
        try {
            users = new ArrayList<>();
            List<Map<String, Object>> userRows = jdbcTemplate.queryForList(query, costCenterId, businessUnitId);
            for (Map<String, Object> row : userRows) {
                User user = new User();

                user.setId(((Long) row.get("id")));
                user.setOsuId((String) row.get("osu_id"));
                user.setFirstName((String) row.get("first_name"));
                user.setMiddleName((String) row.get("middle_name"));
                user.setLastName((String) row.get("last_name"));
                user.setEmail((String) row.get("email"));
                user.setBusinessUnitId((Integer) row.get("business_unit_id"));
                user.setCostCenterId((Integer) row.get("cost_center_id"));
                user.setBusinessUnitName((String) row.get("business_unit"));
                user.setCostCenterName((String) row.get("cost_center"));
                user.setUrl((String) row.get("image_url"));
                user.setRole((String) row.get("role"));
                user.setLicenseList(getUserLicenses(user.getId(), jdbcTemplate));
                user.setTrainer((boolean) row.get("is_trainer"));
                user.setCertifications(getUserCertifications(user.getId(), jdbcTemplate));

                users.add(user);
            }
            return users;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<License> getUserLicenses(Long userId, JdbcTemplate jdbcTemplate) {
        String query = "SELECT ul.*, " +
                "       l.NAME, " +
                "       l.description " +
                "FROM   "+ Constants.TABLE_USER_LICENSE+" AS ul " +
                "       INNER JOIN "+Constants.TABLE_LICENSE+" AS l " +
                "               ON ul.license_id = l.id " +
                "WHERE  ul.user_id = ?";

        List<License> licenses = new ArrayList<>();

        try {
            List<Map<String, Object>> userRows = jdbcTemplate.queryForList(query, userId);
            for (Map<String, Object> row : userRows) {
                License temp = new License();

                temp.setId((Long) row.get("license_id"));
                temp.setName((String) row.get("name"));
                temp.setDescription((String) row.get("description"));

                licenses.add(temp);
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return licenses;
    }

    public List<Certification> getUserCertifications(Long userId, JdbcTemplate jdbcTemplate) {
        String query = "SELECT uc.*, " +
                "       c.name, " +
                "       c.description " +
                "FROM   "+ Constants.TABLE_USER_CERTIFICATION + " AS uc " +
                "       INNER JOIN "+Constants.TABLE_CERTIFICATIONS + " AS c " +
                "               ON uc.cert_id = c.id " +
                "WHERE  uc.user_id = ?";

        List<Certification> certifications = new ArrayList<>();

        try {
            List<Map<String, Object>> userRows = jdbcTemplate.queryForList(query, userId);
            for (Map<String, Object> row : userRows) {
                Certification temp = new Certification();

                temp.setId((Integer) row.get("cert_id"));
                temp.setName((String) row.get("name"));
                temp.setDescription((String) row.get("description"));
                Date date = (Date) row.get("expiry");
                if (date != null)
                    temp.setExpiry(date.toLocalDate());
                certifications.add(temp);
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return certifications;
    }

    private void insertUserLicenses(Long userId, List<License> licenses, JdbcTemplate jdbcTemplate) {
        String query = "INSERT INTO " + Constants.TABLE_USER_LICENSE + " VALUES (?,?)";

        for (License license: licenses) {
            jdbcTemplate.update(query, userId, license.getId());
        }
    }

    private void deleteUserLicense(Long userId, JdbcTemplate jdbcTemplate) {
        String query = "DELETE FROM " + Constants.TABLE_USER_LICENSE +
                " WHERE  user_id = ? ";
        jdbcTemplate.update(query, userId);
    }

    private void deleteCurrentCertifications(Long userId, JdbcTemplate jdbcTemplate) {
        String query = "DELETE FROM " + Constants.TABLE_USER_CERTIFICATION + " WHERE  user_id = ? ";
        jdbcTemplate.update(query, userId);
    }
}
