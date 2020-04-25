package com.klarite.backend.service.impl;

import com.klarite.backend.Constants;
import com.klarite.backend.dto.Episode;
import com.klarite.backend.dto.SkillEpisode;
import com.klarite.backend.dto.User;
import com.klarite.backend.service.UserService;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public ResponseEntity<Object> addSkillEpisode(Episode episode, JdbcTemplate jdbcTemplate) {
        try {
            String query = "INSERT INTO" + Constants.TABLE_EPISODES + "VALUES(?, ?, ?, ?); SELECT SCOPE_IDENTITY() as id;";
            Map<String, Object> row = jdbcTemplate.queryForMap(query, episode.getUserId(), episode.getDate(),
                    episode.getMrn(), episode.isAudited());
            BigDecimal episodeId = (BigDecimal) row.get("id");
            insertSkillEpisodes(episodeId.longValue(), episode.getEpisodes(), jdbcTemplate);
            return new ResponseEntity<>("Stored", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Object> updateSkillEpisode(Episode episode, JdbcTemplate jdbcTemplate) {
        try {
            String query = "UPDATE" + Constants.TABLE_EPISODES + "SET date = ?, mrn = ?, is_audited = ? WHERE id = ?;";
            jdbcTemplate.update(query, episode.getDate(), episode.getMrn(), episode.isAudited(), episode.getId());
            insertSkillEpisodes(episode.getId().longValue(), episode.getEpisodes(), jdbcTemplate);
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
    public List<User> getAllUsers(JdbcTemplate jdbcTemplate) {
        String query = "SELECT u.*, " +
                "       b.NAME as business_unit, " +
                "       c.NAME as cost_center" +
                " FROM   " + Constants.TABLE_USERS + " AS u, " +
                "       " + Constants.TABLE_COST_CENTER + " AS c, " +
                "       " + Constants.TABLE_BUSINESS_UNIT + " AS b " +
                " WHERE  u.cost_center_id = c.id " +
                "       AND u.business_unit_id = b.id ";
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
                user.setTrainer((boolean) row.get("is_trainer"));

                users.add(user);
            }
            return users;
        } catch (Exception e) {
            return new ArrayList<>();
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
                "       AND u.id = ?";
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
                    " VALUES(?,?,?,?,?,?,?,?,?,?,?,?);";
            try {
                jdbcTemplate.update(insertUser, user.getOsuId(), user.getFirstName(), user.getMiddleName(),
                        user.getLastName(), user.getEmail(), "5f4dcc3b5aa765d61d8327deb882cf99",
                        user.getCostCenterId(), user.getBusinessUnitId(), "./image8.jpg", user.getRole(),
                        user.isTrainer(), true);
                return new ResponseEntity<>("Updated", HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
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

            ResponseEntity<Object> response = new ResponseEntity<>("Updated", HttpStatus.CREATED);
            return response;
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private void insertSkillEpisodes(long episodeId, List<SkillEpisode> skillEpisodeList, JdbcTemplate jdbcTemplate) {
        deleteExisitingEPisodes(episodeId, jdbcTemplate);

        String query = "INSERT INTO" + Constants.TABLE_SKILL_EPISODES + "VALUES(?, ?, ?, ?);";
        for (SkillEpisode skillEpisode : skillEpisodeList) {
            if (!skillEpisode.isObserved() || skillEpisode.getObserverId() == 0)
                jdbcTemplate.update(query, episodeId, skillEpisode.getSkillId(), skillEpisode.isObserved(), null);
            else
                jdbcTemplate.update(query, episodeId, skillEpisode.getSkillId(), skillEpisode.isObserved(), skillEpisode.getObserverId());
        }
    }

    private void deleteExisitingEPisodes(long episodeId, JdbcTemplate jdbcTemplate) {
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
}
