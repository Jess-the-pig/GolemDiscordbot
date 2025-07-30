package Golem.api.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCharacters is a Querydsl query type for Characters
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCharacters extends EntityPathBase<Characters> {

    private static final long serialVersionUID = 862879009L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCharacters characters = new QCharacters("characters");

    public final StringPath background = createString("background");

    public final QCampaign campaign;

    public final StringPath characterName = createString("characterName");

    public final StringPath class_ = createString("class_");

    public final DateTimePath<java.time.LocalDateTime> dateCreated = createDateTime("dateCreated", java.time.LocalDateTime.class);

    public final NumberPath<Integer> experiencePoints = createNumber("experiencePoints", Integer.class);

    public final StringPath featuresAndTraits = createString("featuresAndTraits");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath languages = createString("languages");

    public final DateTimePath<java.time.LocalDateTime> lastUpdated = createDateTime("lastUpdated", java.time.LocalDateTime.class);

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final StringPath personalityTraits = createString("personalityTraits");

    public final StringPath playerName = createString("playerName");

    public final StringPath race = createString("race");

    public QCharacters(String variable) {
        this(Characters.class, forVariable(variable), INITS);
    }

    public QCharacters(Path<? extends Characters> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCharacters(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCharacters(PathMetadata metadata, PathInits inits) {
        this(Characters.class, metadata, inits);
    }

    public QCharacters(Class<? extends Characters> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.campaign = inits.isInitialized("campaign") ? new QCampaign(forProperty("campaign")) : null;
    }

}

