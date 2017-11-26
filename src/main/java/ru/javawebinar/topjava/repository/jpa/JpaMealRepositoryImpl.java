package ru.javawebinar.topjava.repository.jpa;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class JpaMealRepositoryImpl implements MealRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) throws NotFoundException {
        if (meal.isNew()) {
            User ref = em.getReference(User.class, userId);
            meal.setUser(ref);
            em.persist(meal);
        } else {
            try {
                if (em.createNamedQuery(Meal.UPDATE)
                        .setParameter("description", meal.getDescription())
                        .setParameter("calories", meal.getCalories())
                        .setParameter("date_time", meal.getDateTime())
                        .setParameter("user_id", userId)
                        .setParameter("id", meal.getId()).
                                executeUpdate() == -1) {
                    throw new NotFoundException("meal not found");
                }
            } catch (Exception e) {
                throw new NotFoundException("meal not found");
            }
        }
        return meal;
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        return em.createNamedQuery(Meal.DELETE)
                .setParameter("idm", id)
                .setParameter("user_id", userId)
                .executeUpdate() != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        try {
            Meal m = em.createNamedQuery(Meal.GET, Meal.class)
                    .setParameter("idm", id)
                    .setParameter("user_id", userId)
                    .getSingleResult();
            return m;
        } catch (NoResultException nre) {
            throw new NotFoundException("not found exception");
        }
    }

    @Override
    public List<Meal> getAll(int userId) {
        return em.createNamedQuery(Meal.GETALL, Meal.class)
                .setParameter("user_id", userId)
                .getResultList();
    }

    @Override
    public List<Meal> getBetween(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        List<Meal> l = new ArrayList<>();
        try {
            l = em.createNamedQuery(Meal.GETBETWEEN, Meal.class)
                    .setParameter("user_id", userId)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();
        } finally {
            return l;
        }
    }
}