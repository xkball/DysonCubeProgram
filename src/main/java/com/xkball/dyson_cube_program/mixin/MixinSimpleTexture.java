package com.xkball.dyson_cube_program.mixin;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.client.resources.metadata.TextureMetadataUsingMipmap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MipmapGenerator;
import net.minecraft.client.renderer.texture.MipmapStrategy;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.client.blaze3d.validation.ValidationGpuTexture;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;

@NonNullByDefault
@Mixin(SimpleTexture.class)
public abstract class MixinSimpleTexture extends ReloadableTexture {
    
    @Unique
    private TextureMetadataUsingMipmap dysonCubeProgram$usingMipmap = null;
    
    public MixinSimpleTexture(Identifier resourceId) {
        super(resourceId);
    }
    
    @Override
    protected void doLoad(NativeImage image) {
        if(this.dysonCubeProgram$usingMipmap != null){
            this.close();
            var gpudevice = RenderSystem.getDevice();
            this.texture = gpudevice.createTexture(this.resourceId()::toString, GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_TEXTURE_BINDING, TextureFormat.RGBA8, image.getWidth(), image.getHeight(), 1, this.dysonCubeProgram$usingMipmap.mipmapLevels()+1);
            this.textureView = gpudevice.createTextureView(this.texture);
            gpudevice.createCommandEncoder().writeToTexture(this.texture, image);
            var textureId = -1;
            if(this.texture instanceof GlTexture glTexture) textureId = glTexture.id;
            else if(this.texture instanceof ValidationGpuTexture validationGpuTexture) textureId = ((GlTexture)validationGpuTexture.getRealTexture()).id;
            else {
                throw new IllegalStateException("Cannot get texture id from: " + this.texture);
            }
            if(textureId != -1){
                var w = image.getWidth();
                var h = image.getHeight();
                if(this.dysonCubeProgram$usingMipmap.useGLGenerateMipmap()){
                    var old = GL30.glGetInteger(GL30.GL_TEXTURE_BINDING_2D);
                    GL30.glBindTexture(GL30.GL_TEXTURE_2D,textureId);
                    GL30.glGenerateMipmap(textureId);
                    GL30.glBindTexture(GL30.GL_TEXTURE_2D,old);
                }
                else{
                    var byMipLevel = new NativeImage[]{image};
                    byMipLevel = MipmapGenerator.generateMipLevels(this.resourceId(),byMipLevel, this.dysonCubeProgram$usingMipmap.mipmapLevels(), MipmapStrategy.CUTOUT, 0.1F);
                    for(var i = 1; i < byMipLevel.length; i++){
                        if(!this.dysonCubeProgram$usingMipmap.manualMipmaps().containsKey(Integer.toString(i))){
                            gpudevice.createCommandEncoder().writeToTexture(this.texture, byMipLevel[i],i,0,0,0,w >> i,h >> i, 0,0);
                        }
                        if(byMipLevel[i] != image){
                            byMipLevel[i].close();
                        }
                    }
                }
                for (int i = 1; i < this.dysonCubeProgram$usingMipmap.mipmapLevels() + 1; i++) {
                    var rl = this.dysonCubeProgram$usingMipmap.manualMipmaps().get(Integer.toString(i));
                    if(rl != null){
                        try{
                            var resource = Minecraft.getInstance().getResourceManager().getResourceOrThrow(rl);
                            NativeImage nativeimage;
                            try (InputStream inputstream = resource.open()) {
                                nativeimage = NativeImage.read(inputstream);
                            }
                            gpudevice.createCommandEncoder().writeToTexture(this.texture, nativeimage,i,0,0,0,w >> i,h >> i, 0,0);
                            nativeimage.close();
                        }catch (IOException e){
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        else {
            super.doLoad(image);
        }
    }
    
    @Inject(method = "loadContents",at = @At("RETURN"))
    public void onLoad(ResourceManager resourceManager, CallbackInfoReturnable<TextureContents> cir) throws IOException {
        var resource = resourceManager.getResourceOrThrow(this.resourceId());
        this.dysonCubeProgram$usingMipmap = resource.metadata().getSection(TextureMetadataUsingMipmap.TYPE).orElse(null);
    }
}
