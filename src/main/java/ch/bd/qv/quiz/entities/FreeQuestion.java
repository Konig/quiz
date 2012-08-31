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
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author thierry
 */
@Entity
@DiscriminatorValue("FREE")
@NamedQueries({@NamedQuery(name="countFreeQuestion", query = "Select Count (x) from FreeQuestion x "),
    @NamedQuery(name="getFreeQuestion", query = "Select x from FreeQuestion x ")})
public class FreeQuestion extends BaseQuestion implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String answer; 

    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    
    public String getAnswer()
    {
        return answer; 
    }
   
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FreeQuestion)) {
            return false;
        }
        FreeQuestion other = (FreeQuestion) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ch.bd.qv.quiz.entities.RadioQuestion[ id=" + getId() + " ]";
    }
    
}
