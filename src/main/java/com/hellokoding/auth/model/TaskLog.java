package com.hellokoding.auth.model;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.*;

@Entity
public class TaskLog {

    @Id
    @Column(name = "ID_TASKLOG")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @OneToOne(fetch = FetchType.EAGER)
    private Task task;

    @Lob
    private StringBuilder log = new StringBuilder();

    @Transient
    private Task previousTask;

    @Transient
    private List<String> filter = new ArrayList<>(Arrays.asList("subject", "description", "text", "date", "price", "state", "executor"));

    public TaskLog() {
    }

    public TaskLog(Task task) {
        this.task = task;
    }

    public void saveUnchangedTask() throws CloneNotSupportedException {
        previousTask = (Task) task.clone();
    }

    public void compareTasks() throws IllegalAccessException {
        log.append("<i>");
        log.append(new Date().toString());
        if (previousTask == null)
            log.append("</i>&nbsp&nbsp&nbsp&nbsp<b>Task created</b><br/>");
        else
            log.append("</i>&nbsp&nbsp&nbsp&nbsp<b>Task updated</b><br/>");
        Field[] fields = task.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            final String tab = "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
            String fieldName = field.getName();
            Object fieldValue = field.get(task);
            Object previousFieldValue;
            if (previousTask == null)
                previousFieldValue = null;
            else
                previousFieldValue = field.get(previousTask);
            if (fieldValue != null) {
                if (filter.contains(fieldName))
                    if (!fieldValue.equals(previousFieldValue)) {
                        log.append(tab);
                        log.append(fieldName);
                        if (previousTask == null)
                            log.append(" set to ");
                        else log.append(" changed to ");
                        log.append(fieldValue.toString());
                        log.append("<br/>");
                    }

//                if (previousFieldValue != null && !fieldValue.equals(previousFieldValue))
//                    switch (fieldName) {
//                        case "manager":
//                            log.append(tab);
//                            log.append("User ");
//                            log.append(((User) fieldValue).getUsername());
//                            log.append(" became executor");
//                            break;
//                        case "candidates":
//                            log.append(tab);
//                            User candidate = compareSets((Set<User>) fieldValue, (Set<User>) previousFieldValue);
//                            log.append("User ");
//                            log.append(candidate.getUsername());
//                            log.append(" accepted task");
//                            break;
//                    }
            }
        }
    }

//    private <T> T compareSets(Set<T> set, Set<T> previousSet) {
//        if (!set.containsAll(previousSet)) {
//            Set<T> result = new HashSet<>(set);
//            result.retainAll(previousSet);
//            return result.iterator().next();
//        } else {
//            Set<T> result = new HashSet<>(previousSet);
//            result.retainAll(set);
//            return result.iterator().next();
//        }
//    }

    public String getLogStr() {
        return log.toString();
    }
}
