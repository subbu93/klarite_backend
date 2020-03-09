package com.klarite.backend.service.impl;

import com.klarite.backend.Constants;
import com.klarite.backend.dto.TrainingAssignment;
import com.klarite.backend.service.TrainingAssignmentService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TrainingAssignmentServiceImpl implements TrainingAssignmentService {
    @Override
    public List<TrainingAssignment> getAllAssignedTrainings(long userId, JdbcTemplate jdbcTemplate) {
        String query = "SELECT t3.*, " +
                "       trainings.NAME AS training_name " +
                "FROM   (SELECT t2.*, " +
                "               ta.training_id, " +
                "               ta.date, " +
                "               ta.start_time, " +
                "               ta.location " +
                "        FROM   (SELECT t1.user_id, " +
                "                       t1.first_name, " +
                "                       t1.middle_name, " +
                "                       t1.last_name, " +
                "                       t1.cost_center_name, " +
                "                       bu.NAME AS business_unit_name " +
                "                FROM   (SELECT u.id    AS user_id, " +
                "                               u.first_name, " +
                "                               u.middle_name, " +
                "                               u.last_name, " +
                "                               u.business_unit_id, " +
                "                               cc.NAME AS cost_center_name " +
                "                        FROM   "+ Constants.TABLE_USERS +" AS u, " +
                "                              "+Constants.TABLE_COST_CENTER +" AS cc " +
                "                        WHERE  u.cost_center_id = cc.id " +
                "                               AND u.id = ?) AS t1, " +
                "                       "+Constants.TABLE_BUSINESS_UNIT+" AS bu " +
                "                WHERE  t1.business_unit_id = bu.id) AS t2, " +
                "               "+Constants.TABLE_TRAINING_ASSIGNMENTS+" AS ta " +
                "        WHERE  ta.user_id = t2.user_id) AS t3, " + Constants.TABLE_TRAININGS +
                " WHERE  trainings.id = t3.training_id" ;

        List<TrainingAssignment> trainingAssignments = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, userId);

        for (Map<String, Object> row : rows) {
            TrainingAssignment trainingAssignment = new TrainingAssignment();

            trainingAssignment.setBusinessUnit((String) row.get("business_unit_name"));
            trainingAssignment.setCostCenter((String) row.get("cost_center_name"));
            trainingAssignment.setStartTime((byte[]) row.get("start_time"));
            trainingAssignment.setDate((Date) row.get("date"));
            trainingAssignment.setLocation((String) row.get("location"));
            trainingAssignment.setTrainingId((Long) row.get("training_id"));
            trainingAssignment.setTrainingName((String) row.get("training_name"));

            trainingAssignments.add(trainingAssignment);
        }
        return trainingAssignments;
    }
}
