package net.frank.mc3ver.tree;

import net.frank.mc3ver.grove.OldPearTreeLogic.Carving;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * Stammblock des alten Birnbaums mit Inschrift in der Rinde. Zwei Blöcke nebeneinander tragen je eine Hälfte;
 * {@code FACING} zeigt zur beschrifteten Seite. Abgebaut wird daraus ein gewöhnlicher Birnenstamm.
 */
public class PearBarkBlock extends Block {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty RIGHT = BooleanProperty.create("right");
    public static final EnumProperty<Carving> CARVING = EnumProperty.create("carving", Carving.class);

    public PearBarkBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition
                .any()
                .setValue(FACING, Direction.NORTH)
                .setValue(RIGHT, false)
                .setValue(CARVING, Carving.PETER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, RIGHT, CARVING);
    }
}
