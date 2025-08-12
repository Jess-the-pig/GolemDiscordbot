package Golem.api.rpg.spells;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSpells is a Querydsl query type for Spells
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSpells extends EntityPathBase<Spells> {

    private static final long serialVersionUID = 1702427891L;

    public static final QSpells spells = new QSpells("spells");

    public final StringPath casting_time = createString("casting_time");

    public final StringPath casting_time_misc = createString("casting_time_misc");

    public final StringPath component_material = createString("component_material");

    public final StringPath component_misc = createString("component_misc");

    public final StringPath component_semantic = createString("component_semantic");

    public final StringPath component_verbal = createString("component_verbal");

    public final StringPath description = createString("description");

    public final StringPath duration = createString("duration");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath level = createString("level");

    public final StringPath name = createString("name");

    public final StringPath range = createString("range");

    public final StringPath range_area = createString("range_area");

    public final StringPath school = createString("school");

    public final StringPath school_ritual = createString("school_ritual");

    public QSpells(String variable) {
        super(Spells.class, forVariable(variable));
    }

    public QSpells(Path<? extends Spells> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSpells(PathMetadata metadata) {
        super(Spells.class, metadata);
    }

}

