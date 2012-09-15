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
package ch.bd.qv.quiz.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author thierry
 */
@Entity
public class QuizResult implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn
    private Person person;
    
    @ManyToMany(cascade= CascadeType.REMOVE)
    @JoinTable(name = "QR_QUESTION_TRANSLATION", joinColumns =
    @JoinColumn(name = "QUIZ_RESULT_ID"), inverseJoinColumns =
    @JoinColumn(name = "QUESTION_ID"))
    private List<BaseQuestion> questions = new ArrayList<>();
    
    @ManyToMany(cascade= CascadeType.REMOVE)
    @JoinTable(name = "QR_QUESTION_WRONG_TRANSLATION", joinColumns =
    @JoinColumn(name = "QUIZ_RESULT_ID"), inverseJoinColumns =
    @JoinColumn(name = "WRONG_ANSWER_ID"))
    private List<WrongAnswer> wrongAnswers = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition="VARCHAR(255) default 'NOT_STARTED'")
    private QuizStateEnum quizState; 
    
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar startTime; 
    
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar endTime; 

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<BaseQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<BaseQuestion> questions) {
        this.questions = questions;
    }

    public List<WrongAnswer> getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(List<WrongAnswer> wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public QuizStateEnum getQuizState() {
        return quizState;
    }

    public void setQuizState(QuizStateEnum quizState) {
        this.quizState = quizState;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof QuizResult)) {
            return false;
        }
        QuizResult other = (QuizResult) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ch.bd.qv.quiz.entities.QuizResult[ id=" + id + " ]";
    }
}
