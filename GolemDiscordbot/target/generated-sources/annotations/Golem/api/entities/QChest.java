package Golem.api.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChest is a Querydsl query type for Chest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChest extends EntityPathBase<Chest> {

    private static final long serialVersionUID = -2027422230L;

    public static final QChest chest = new QChest("chest");

    public final ListPath<Equipments, QEquipments> equipementloot = this.<Equipments, QEquipments>createList("equipementloot", Equipments.class, QEquipments.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QChest(String variable) {
        super(Chest.class, forVariable(variable));
    }

    public QChest(Path<? extends Chest> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChest(PathMetadata metadata) {
        super(Chest.class, metadata);
    }

}

