package com.steepmax.android.wallpapers;

import com.badlogic.gdx.backends.android.livewallpaper.AndroidApplicationLW;

import com.eightbitmage.gdxlw.LibdgxWallpaperService;

public class Wallpaper3dWallpaperService extends LibdgxWallpaperService {

        @Override
        public Engine onCreateEngine() {
                return new ExampleLibdgxWallpaperEngine(this);
        }

        public class ExampleLibdgxWallpaperEngine extends LibdgxWallpaperEngine {

                public ExampleLibdgxWallpaperEngine(
                                LibdgxWallpaperService libdgxWallpaperService) {
                        super(libdgxWallpaperService);
                }

                @Override
                protected void initialize(AndroidApplicationLW androidApplicationLW) {

                		Flag3D flag = new Flag3D();
                        setWallpaperListener(flag);
                        androidApplicationLW.initialize(flag, false);

                }
        }
}