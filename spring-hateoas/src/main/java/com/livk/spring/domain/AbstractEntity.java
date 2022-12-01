package com.livk.spring.domain;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

/**
 * <p>
 * AbstractEntity
 * </p>
 *
 * @author livk
 * @date 2022/5/4
 */
public abstract class AbstractEntity<T> extends RepresentationModel<AbstractEntity<T>> {

    protected AbstractEntity() {
        super();
    }

    protected AbstractEntity(Link initialLink) {
        super(initialLink);
    }

    protected AbstractEntity(Iterable<Link> initialLinks) {
        super(initialLinks);
    }

}
