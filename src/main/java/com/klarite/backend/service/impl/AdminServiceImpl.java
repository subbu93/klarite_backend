package com.klarite.backend.service.impl;

import com.klarite.backend.Constants;
import com.klarite.backend.dto.ContactHours;
import com.klarite.backend.dto.Skill;
import com.klarite.backend.dto.Training;
import com.klarite.backend.dto.User;
import com.klarite.backend.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Override
    public List<Skill> getAllSkills(JdbcTemplate jdbcTemplate) {
        String query = "SELECT sk.id " +
                "      ,sk.name" +
                "      ,sk.description" +
                "      ,sk.threshold" +
                "      ,t.name as training_prerequisite" +
                "      ,t.id as training_id" +
                "  FROM " + Constants.TABLE_SKILLS + " as sk, " + Constants.TABLE_TRAININGS + " as t" +
                "  WHERE t.id = sk.training_prerequisite_id " +
                "  AND sk.soft_delete = 0";
        List<Skill> skills = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);

        for (Map<String, Object> row : rows) {
            Skill obj = new Skill();

            obj.setId(((Long) row.get("id")));
            obj.setSkillName((String) row.get("name"));
            obj.setDescription((String) row.get("description"));
            obj.setThreshold((Integer) row.get("threshold"));
            obj.setSkillTrainingPreRequisite((String) row.get("training_prerequisite"));
            obj.setTrainingId((Long) row.get("training_id"));
            skills.add(obj);
        }
        return skills;
    }

    @Override
    public Skill getSkill(long id, JdbcTemplate jdbcTemplate) {
        String query = "SELECT sk.id " +
                "      ,sk.name" +
                "      ,sk.description" +
                "      ,sk.threshold" +
                "      ,t.name as Training_prerequisite" +
                "      ,t.id as training_id" +
                "     FROM skills as sk, trainings as t" +
                "     WHERE sk.id = " + id + " AND " +
                "      t.id = sk.training_prerequisite_id" +
                "      AND sk.soft_delete = 0";

        Map<String, Object> row = jdbcTemplate.queryForMap(query);

        Skill skill = new Skill();

        skill.setId(((Long) row.get("id")));
        skill.setSkillName((String) row.get("name"));
        skill.setDescription((String) row.get("description"));
        skill.setThreshold((Integer) row.get("threshold"));
        skill.setSkillTrainingPreRequisite((String) row.get("Training_prerequisite"));
        skill.setTrainingId((Long) row.get("training_id"));
        return skill;
    }

    @Override
    public ResponseEntity<Object> addSkill(Skill skill, JdbcTemplate jdbcTemplate) {
        String query = "";
        if (skill.getId() == null) {
            query = "INSERT INTO " + Constants.TABLE_SKILLS +
                    " (name, description, threshold,training_prerequisite_id, soft_delete) " +
                    " VALUES      (?," +
                    "             ?, " +
                    "             ?," +
                    "             ?," +
                    "             0); ";
            try {
                jdbcTemplate.update(query, skill.getSkillName(), skill.getDescription(), skill.getThreshold(),
                        skill.getTrainingId());
                ResponseEntity<Object> response = new ResponseEntity<>("Stored", HttpStatus.CREATED);
                return response;
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            query = "UPDATE " + Constants.TABLE_SKILLS +
                    " SET name = ?, description= ?, threshold = ?, training_prerequisite_id = ? " +
                    " WHERE id = ?;";
            try {
                jdbcTemplate.update(query, skill.getSkillName(), skill.getDescription(), skill.getThreshold(),
                        skill.getTrainingId(), skill.getId());
                ResponseEntity<Object> response = new ResponseEntity<>("Stored", HttpStatus.CREATED);
                return response;
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Override
    public ResponseEntity<Object> deleteSkill(long skillId, JdbcTemplate jdbcTemplate) {
        String query = "UPDATE " + Constants.TABLE_SKILLS +
                " SET soft_delete = 1 " +
                " WHERE id = ?";
        try {
            jdbcTemplate.update(query, skillId);
            return new ResponseEntity<>("Deleted Skill", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<Training> getAllTrainings(JdbcTemplate jdbcTemplate) {
        String query = "SELECT tr.*, " +
                "       u.first_name, " +
                "       u.last_name " +
                "FROM   " + Constants.TABLE_TRAININGS + " AS tr, " +
                "       " + Constants.TABLE_USERS + " AS u " +
                "WHERE  tr.trainer_id = u.id " +
                "AND tr.soft_delete = " + Constants.ZERO;

        List<Training> trainings = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);

        for (Map<String, Object> row : rows) {
            Training obj = new Training();

            obj.setId(((Long) row.get("id")));
            obj.setTrainingName((String) row.get("name"));
            obj.setDescription((String) row.get("description"));
            obj.setCE((Boolean) row.get("is_ce"));
            obj.setCeId((Long) row.get("ce_id"));
            obj.setTrainerId((Long) row.get("trainer_id"));
            obj.setTrainerName((String) row.get("first_name") + " " + (String) row.get("last_name"));
            obj.setTotalHours((Integer) row.get("total_hours"));

            trainings.add(obj);
        }
        return trainings;
    }

    @Override
    public Training getTraining(long trainingId, JdbcTemplate jdbcTemplate) {
        String query = "SELECT tr.*, " +
                "       u.first_name, " +
                "       u.last_name " +
                " FROM   " + Constants.TABLE_TRAININGS + " AS tr, " +
                "       " + Constants.TABLE_USERS + " AS u " +
                " WHERE  tr.id = " + trainingId +
                " AND tr.trainer_id = u.id " +
                " AND tr.soft_delete = " + Constants.ZERO +
                " AND u.soft_delete = "+ Constants.ZERO;

        Map<String, Object> row = jdbcTemplate.queryForMap(query);
        Training training = new Training();

        training.setId(((Long) row.get("id")));
        training.setTrainingName((String) row.get("name"));
        training.setDescription((String) row.get("description"));
        training.setCE((Boolean) row.get("is_ce"));
        training.setCeId((Long) row.get("ce_id"));
        training.setTrainerId((Long) row.get("trainer_id"));
        training.setTrainerName((String) row.get("first_name") + " " + (String) row.get("last_name"));
        training.setTotalHours((Integer) row.get("total_hours"));

        return training;
    }

    @Override
    public List<User> getTrainer(JdbcTemplate jdbcTemplate) {
        String query = "SELECT u.*, " +
                "       b.NAME as business_unit, " +
                "       c.NAME as cost_center" +
                " FROM   users AS u, " +
                "       cost_center AS c, " +
                "       business_unit AS b " +
                " WHERE  u.is_trainer = " + Constants.ONE +
                "       AND u.cost_center_id = c.id " +
                "       AND u.business_unit_id = b.id ";

        List<User> users = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);

        for (Map<String, Object> row : rows) {
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
            user.setTrainer(true);

            users.add(user);
        }

        return users;
    }

    @Override
    public ResponseEntity<Object> addTraining(Training training, JdbcTemplate jdbcTemplate) {
        String query = "";
        if (training.getId() == null) {
            query = "INSERT INTO " + Constants.TABLE_TRAININGS +
                    " (name, description, is_ce, ce_id, trainer_id, total_hours, soft_delete) " +
                    " VALUES      (?," +
                    "             ?, " +
                    "             ?," +
                    "             ?," +
                    "             ?," +
                    "             ?," +
                    "             0); ";
            try {
                jdbcTemplate.update(query, training.getTrainingName(), training.getDescription(), training.isCE(),
                        training.getCeId(), training.getTrainerId(), training.getTotalHours());
                ResponseEntity<Object> response = new ResponseEntity<>("Stored", HttpStatus.CREATED);
                return response;
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            query = "UPDATE " + Constants.TABLE_TRAININGS +
                    " SET name = ?, description= ?, is_ce = ?, ce_id = ?, trainer_id = ?, total_hours = ? " +
                    " WHERE id = ?;";
            try {
                jdbcTemplate.update(query, training.getTrainingName(), training.getDescription(), training.isCE(),
                        training.getCeId(), training.getTrainerId(), training.getTotalHours(), training.getId());
                ResponseEntity<Object> response = new ResponseEntity<>("Stored", HttpStatus.CREATED);
                return response;
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Override
    public ResponseEntity<Object> deleteTraining(long trainingId, JdbcTemplate jdbcTemplate) {
        String query = "UPDATE " + Constants.TABLE_TRAININGS +
                " SET soft_delete = 1 " +
                " WHERE id = ?";
        try {
            jdbcTemplate.update(query, trainingId);
            return new ResponseEntity<>("Deleted Skill", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Object> addContactHours(ContactHours ce, JdbcTemplate jdbcTemplate) {
        String query = "";
        if (ce.getId() == null) {
            query = "INSERT INTO " + Constants.TABLE_CONTINUED_EDUCATION +
                    " (state, title, position , ce_hours_per_year) " +
                    " VALUES      (?," +
                    "             ?, " +
                    "             ?," +
                    "             ?); ";
            try {
                jdbcTemplate.update(query, ce.getState(), ce.getUserTitle(), ce.getPosition(), ce.getCeHrsPerYear());
                ResponseEntity<Object> response = new ResponseEntity<>("Stored", HttpStatus.CREATED);
                return response;
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            query = "UPDATE " + Constants.TABLE_CONTINUED_EDUCATION +
                    " SET state = ?, title = ?, position = ?, ce_hours_per_year = ? " +
                    " WHERE id = ?;";
            try {
                jdbcTemplate.update(query, ce.getState(), ce.getUserTitle(), ce.getPosition(),
                        ce.getCeHrsPerYear(), ce.getId());
                ResponseEntity<Object> response = new ResponseEntity<>("Stored", HttpStatus.CREATED);
                return response;
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Override
    public ContactHours getCeHrs(String state, String title, String position, JdbcTemplate jdbcTemplate) {
        ContactHours ce = new ContactHours();
        if (state == null || title == null || position == null) {
            return null;
        }
        String query = "SELECT * FROM " + Constants.TABLE_CONTINUED_EDUCATION +
                "       WHERE state = ?" +
                "       AND position = ?" +
                "       AND title = ?";
        try {
            Map<String, Object> row = jdbcTemplate.queryForMap(query, state, position, title);

            ce.setId((Long) row.get("id"));
            ce.setPosition((String) row.get("position"));
            ce.setState((String) row.get("state"));
            ce.setUserTitle((String) row.get("title"));
            ce.setCeHrsPerYear((Integer) row.get("ce_hours_per_year"));
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

        return ce;
    }
}
