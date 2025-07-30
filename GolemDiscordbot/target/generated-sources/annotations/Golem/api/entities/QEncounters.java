package Golem.api.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEncounters is a Querydsl query type for Encounters
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEncounters extends EntityPathBase<Encounters> {

    private static final long serialVersionUID = -379088809L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEncounters encounters = new QEncounters("encounters");

    public final QCampaign campaignId;

    public final ListPath<Characters, QCharacters> characters = this.<Characters, QCharacters>createList("characters", Characters.class, QCharacters.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> dateCreated = createDateTime("dateCreated", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<Integer, NumberPath<Integer>> initiative = this.<Integer, NumberPath<Integer>>createList("initiative", Integer.class, NumberPath.class, PathInits.DIRECT2);

    public final BooleanPath isFinished = createBoolean("isFinished");

    public final DateTimePath<java.time.LocalDateTime> lastUpdated = createDateTime("lastUpdated", java.time.LocalDateTime.class);

    public final ListPath<Monsters, QMonsters> monsters = this.<Monsters, QMonsters>createList("monsters", Monsters.class, QMonsters.class, PathInits.DIRECT2);

    public QEncounters(String variable) {
        this(Encounters.class, forVariable(variable), INITS);
    }

    public QEncounters(Path<? extends Encounters> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEncounters(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEncounters(PathMetadata metadata, PathInits inits) {
        this(Encounters.class, metadata, inits);
    }

    public QEncounters(Class<? extends Encounters> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.campaignId = inits.isInitialized("campaignId") ? new QCampaign(forProperty("campaignId")) : null;
    }

}

