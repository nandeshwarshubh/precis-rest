package ind.shubhamn.precisrest.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    private static final Logger logger = LoggerFactory.getLogger(WelcomeController.class);

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String welcomeHtml() {
        logger.trace("Welcome page requested");
        logger.debug("Serving welcome HTML page");
        logger.info("Returning welcome page");
        return """
           <!DOCTYPE html>

                                      <html class="dark" lang="en"><head>
                                      <meta charset="utf-8"/>
                                      <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
                                      <title>Precis Service API Documentation</title>
                                      <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
                                      <link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@300;400;500;600;700&amp;family=JetBrains+Mono:wght@400;700&amp;display=swap" rel="stylesheet"/>
                                      <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght@100..700,0..1&amp;display=swap" rel="stylesheet"/>
                                      <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
                                      <script id="tailwind-config">
                                              tailwind.config = {
                                                  darkMode: "class",
                                                  theme: {
                                                      extend: {
                                                          colors: {
                                                              "primary": "#00ffff",
                                                              "background-light": "#f5f8f8",
                                                              "background-dark": "#0f011a", // Deep purple-black
                                                          },
                                                          fontFamily: {
                                                              "display": ["Space Grotesk", "sans-serif"],
                                                              "mono": ["JetBrains Mono", "monospace"]
                                                          },
                                                          borderRadius: {
                                                              "DEFAULT": "0px",s
                                                              "lg": "0.25rem",s
                                                              "xl": "0.5rem",s
                                                              "full": "9999px"
                                                          },
                                                      },
                                                  },
                                              }
                                          </script>
                                      <style>
                                              body {
                                                  background: linear-gradient(135deg, #0f011a 0%, #000a3a 100%);
                                                  min-height: 100vh;
                                              }
                                              .glass-panel {
                                                  background: rgba(42, 0, 74, 0.4);
                                                  backdrop-filter: blur(12px);
                                                  border: 1px solid rgba(0, 255, 255, 0.1);
                                              }
                                              .ascii-art {
                                                  line-height: 1.1;
                                                  letter-spacing: -1px;
                                                  font-family: 'JetBrains Mono', monospace;
                                                  text-shadow: 0 0 10px rgba(0, 255, 255, 0.4);
                                              }
                                              .terminal-green {
                                                  color: #33FF77;
                                                  text-shadow: 0 0 8px rgba(51, 255, 119, 0.5);
                                              }
                                              .scanline {
                                                  width: 100%;
                                                  height: 2px;
                                                  background: rgba(0, 255, 255, 0.05);
                                                  position: absolute;
                                                  top: 0;
                                                  z-index: 50;
                                                  pointer-events: none;
                                              }
                                          </style>
                                      </head>
                                      <body class="font-display text-white selection:bg-primary selection:text-black overflow-x-hidden">
                                      <!-- Subtle HUD Overlay -->
                                      <div class="fixed inset-0 pointer-events-none border-[12px] border-white/5 z-50"></div>
                                      <div class="relative flex min-h-screen flex-col items-center">
                                      <!-- Navigation -->
                                      <header class="w-full max-w-[1200px] flex items-center justify-between px-6 py-8 z-20">
                                      <div class="flex items-center gap-3">
                                      <span class="material-symbols-outlined text-primary text-3xl">terminal</span>
                                      <span class="text-xl font-bold tracking-tighter uppercase text-primary">Precis</span>
                                      </div>
                                      <nav class="hidden md:flex items-center gap-8 uppercase text-xs font-bold tracking-widest text-primary/70">
                                      <a class="hover:text-primary transition-colors" href="#">Endpoints</a>
                                      <a class="hover:text-primary transition-colors" href="#">Spec</a>
                                      <a class="hover:text-primary transition-colors" href="#">Monitor</a>
                                      <a class="flex items-center gap-1 hover:text-primary transition-colors" href="#">
                                                          GitHub <span class="material-symbols-outlined text-xs">open_in_new</span>
                                      </a>
                                      </nav>
                                      <div class="flex items-center gap-4">
                                      <div class="px-3 py-1 bg-primary/10 border border-primary/30 text-primary text-[10px] font-bold uppercase tracking-tighter">
                                                          Status: Online
                                                      </div>
                                      </div>
                                      </header>
                                      <main class="w-full max-w-[960px] flex flex-col items-center px-4 py-12 relative">
                                      <!-- Hero / ASCII Logo -->
                                      <div class="w-full flex flex-col items-center mb-16">
                                      <pre class="ascii-art text-primary text-[0.6rem] sm:text-[0.7rem] md:text-xs text-center mb-8 select-none">
                                      ██████╗ ██████╗ ███████╗ ██████╗██╗███████╗
                                      ██╔══██╗██╔══██╗██╔════╝██╔════╝██║██╔════╝
                                      ██████╔╝██████╔╝█████╗  ██║     ██║███████╗
                                      ██╔═══╝ ██╔══██╗██╔══╝  ██║     ██║╚════██║
                                      ██║     ██║  ██║███████╗╚██████╗██║███████║
                                      ╚═╝     ╚═╝  ╚═╝╚══════╝ ╚═════╝╚═╝╚══════╝
                                                      </pre>
                                      <div class="inline-flex items-center gap-2 px-4 py-2 glass-panel border border-primary/20 rounded-lg">
                                      <span class="flex size-2 rounded-full bg-[#33FF77] animate-pulse"></span>
                                      <span class="terminal-green font-mono text-sm font-bold">✓ Precis is ready!</span>
                                      </div>
                                      </div>
                                      <!-- Intro -->
                                      <div class="w-full text-center max-w-xl mb-16">
                                      </div>

                                      </body></html>
        """;
    }
}
