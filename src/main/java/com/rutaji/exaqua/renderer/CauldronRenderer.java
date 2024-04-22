package com.rutaji.exaqua.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rutaji.exaqua.tileentity.CauldronTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

/**
 * Used to render fluid in the {@link net.minecraft.block.CauldronBlock cauldron}.
 */
public class CauldronRenderer extends TileEntityRenderer<CauldronTileEntity> {

    private Minecraft minecraft = Minecraft.getInstance();

    public CauldronRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }



    @Override
    public void render(@NotNull CauldronTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        FluidStack fluidToRender = tileEntityIn.GetTank().GetFluidstack();
        if (fluidToRender.isEmpty()){return;}
        FluidAttributes attributes = fluidToRender.getFluid().getAttributes();
        ResourceLocation fluidStill = attributes.getStillTexture();
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(fluidStill);

        IVertexBuilder builder = bufferIn.getBuffer(RenderType.getTranslucent());
        int color = attributes.getColor();
        float a = 1.0F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        matrixStackIn.push();

        Float YLevel = tileEntityIn.GetTank().GetFullness() * 0.94f +0.06f;
        add(builder, matrixStackIn, .19f, YLevel, .81f, sprite.getMinU(), sprite.getMaxV(), r, g, b, a);
        add(builder, matrixStackIn, .81f, YLevel, .81f, sprite.getMaxU(), sprite.getMaxV(), r, g, b, a);
        add(builder, matrixStackIn, .81f, YLevel, .19f, sprite.getMaxU(), sprite.getMinV(), r, g, b, a);
        add(builder, matrixStackIn, .19f, YLevel, .19f, sprite.getMinU(), sprite.getMinV(), r, g, b, a);

        matrixStackIn.pop();
    }
    private void add(IVertexBuilder renderer, MatrixStack stack, float x, float y, float z, float u, float v, float r, float g, float b, float a) {
        renderer.pos(stack.getLast().getMatrix(), x, y, z)
                .color(r, g, b, a)
                .tex(u, v)
                .lightmap(0, 240)
                .normal(1, 0, 0)
                .endVertex();
    }



}
