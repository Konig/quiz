/*
 * Copyright 2012 thierry.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.bd.qv.quiz.ejb;

import ch.bd.qv.quiz.app.QuizSessionData;
import ch.bd.qv.quiz.entities.Person;
import ch.bd.qv.quiz.entities.QuizResult;
import com.google.common.base.Preconditions;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;

/**
 *
 * @author thierry
 */
@LocalBean
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PersonBean {
    
    
    private static final Logger LOGGER = Logger.getLogger(PersonBean.class);
    @Inject
    private QuizSessionData data;
    
    
    @Inject
    private QuestionBean questionBean; 
    
    @PersistenceContext
    private EntityManager em;
    
    public Person getPersonByEmail(String email) {
        return em.createNamedQuery("getByEmail", Person.class).setParameter("email", email).getSingleResult();
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Person store(Person p) {
        Preconditions.checkNotNull(p);
        if (p.getId() == null || p.getId() == 0L) {
            em.persist(p);
            return p;
        } else {
            return em.merge(p);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void storePersonAndInitQuizResult(Person p) {
        store(p);
        questionBean.startQuiz(p);
    }

    /**
     * class interceptor for default nre to null
     */
    @AroundInvoke
    protected Object interceptNoResultException(InvocationContext ctx) throws Exception {
        try {
            return ctx.proceed();
        } catch (NoResultException nre) {
            LOGGER.debug("noresultexception caught, return null; " + nre.getCause());
            return null;
        }
    }
}
