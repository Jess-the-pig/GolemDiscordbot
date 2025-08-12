package Golem.api.rpg.loot.generateeuipements;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEquipments is a Querydsl query type for Equipments
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEquipments extends EntityPathBase<Equipments> {

    private static final long serialVersionUID = -524638356L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEquipments equipments = new QEquipments("equipments");

    public final Golem.api.rpg.loot.generatechest.QChest chest;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> price_golds = createNumber("price_golds", Integer.class);

    public final StringPath type1 = createString("type1");

    public final StringPath type2 = createString("type2");

    public final NumberPath<Integer> weight_lbs = createNumber("weight_lbs", Integer.class);

    public QEquipments(String variable) {
        this(Equipments.class, forVariable(variable), INITS);
    }

    public QEquipments(Path<? extends Equipments> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEquipments(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEquipments(PathMetadata metadata, PathInits inits) {
        this(Equipments.class, metadata, inits);
    }

    public QEquipments(Class<? extends Equipments> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.chest = inits.isInitialized("chest") ? new Golem.api.rpg.loot.generatechest.QChest(forProperty("chest")) : null;
    }

}

