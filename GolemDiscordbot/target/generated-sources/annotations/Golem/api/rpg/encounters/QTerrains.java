package Golem.api.rpg.encounters;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTerrains is a Querydsl query type for Terrains
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTerrains extends EntityPathBase<Terrains> {

    private static final long serialVersionUID = -981856687L;

    public static final QTerrains terrains = new QTerrains("terrains");

    public final DateTimePath<java.time.LocalDateTime> dateCreated = createDateTime("dateCreated", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> lastUpdated = createDateTime("lastUpdated", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public QTerrains(String variable) {
        super(Terrains.class, forVariable(variable));
    }

    public QTerrains(Path<? extends Terrains> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTerrains(PathMetadata metadata) {
        super(Terrains.class, metadata);
    }

}

