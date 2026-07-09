package frc.robot;

/**
 * ============================================================
 *  MAPA DO CAMPO - Plano Cartesiano (unidade: centímetros)
 * ============================================================
 *
 *  Y
 *  ^
 *  |
 *  |   [CAMPO]
 *  |
 *  +-----------> X
 *  (0,0) = canto inferior esquerdo do campo
 *
 *  COMO CONFIGURAR:
 *
 *  1. CAMPO: defina largura e altura em cm
 *
 *  2. POSIÇÃO INICIAL: ponto (x, y) em cm + ângulo inicial em graus
 *
 *  3. OBSTÁCULOS: cada obstáculo é um retângulo definido por 4 pontos
 *     Formato: { x_min, y_min, x_max, y_max } em cm
 *     (canto inferior esquerdo -> canto superior direito)
 *     Exemplo:  { 50, 30, 80, 60 } -> retângulo de 30x30cm com canto em (50,30)
 *
 *  4. WAYPOINTS (objetivos sequenciais): lista de pontos (x, y) em cm
 *     O robô vai do ponto inicial -> wp[0] -> wp[1] -> ... -> wp[n]
 *     Opcionalmente: ângulo final desejado ao chegar em cada waypoint (-999 = não girar)
 *
 * ============================================================
 */
public final class FieldMap {

    // =========================================================
    //  DIMENSÕES DO CAMPO (cm)
    // =========================================================
    public static final double CAMPO_LARGURA_CM = 300.0;  // eixo X
    public static final double CAMPO_ALTURA_CM  = 300.0;  // eixo Y

    // =========================================================
    //  POSIÇÃO INICIAL DO ROBÔ
    //  x, y em cm | anguloInicial em graus (0 = olhando para +X)
    // =========================================================
    public static final double INICIO_X_CM     = 20.0;
    public static final double INICIO_Y_CM     = 20.0;
    public static final double INICIO_ANGULO   = 90.0;  // robô inicia olhando para +Y (frente)

    // =========================================================
    //  OBSTÁCULOS
    //  Cada linha: { x_min, y_min, x_max, y_max } em cm
    //  Adicione ou remova linhas conforme o campo
    // =========================================================
    public static final double[][] OBSTACULOS = {
        //  x_min   y_min   x_max   y_max
        {    80.0,   80.0,  120.0,  120.0 },   // obstáculo 1 - centro
        {   180.0,   40.0,  220.0,   90.0 },   // obstáculo 2 - direita baixo
        {    40.0,  180.0,   90.0,  220.0 },   // obstáculo 3 - esquerda cima
    };

    // =========================================================
    //  WAYPOINTS SEQUENCIAIS
    //  Cada linha: { x_cm, y_cm, angulo_final }
    //  angulo_final: graus que o robô deve ter ao chegar (-999 = não girar)
    //  O robô executa: INICIO -> WP[0] -> WP[1] -> ... -> WP[n]
    // =========================================================
    public static final double[][] WAYPOINTS = {
        //   x      y     angulo_final
        {  150.0,  20.0,    -999  },   // wp 0: vai para X=150, Y=20, sem girar
        {  150.0, 150.0,    90.0  },   // wp 1: vai para X=150, Y=150, termina olhando 90°
        {   20.0, 150.0,   180.0  },   // wp 2: vai para X=20,  Y=150, termina olhando 180°
    };

    // =========================================================
    //  MARGEM DE SEGURANÇA ao redor dos obstáculos (cm)
    //  O robô evita entrar nessa zona ao planejar movimento
    // =========================================================
    public static final double MARGEM_OBSTACULO_CM = 15.0;

    // =========================================================
    //  TOLERÂNCIA DE CHEGADA ao waypoint (cm)
    //  Considera chegado quando estiver a menos disso do alvo
    // =========================================================
    public static final double TOLERANCIA_CHEGADA_CM = 3.0;

    // =========================================================
    //  Utilitário: verifica se um ponto (px, py) está dentro
    //  de algum obstáculo (com margem)
    // =========================================================
    public static boolean dentroDeObstaculo(final double px, final double py) {
        for (final double[] obs : OBSTACULOS) {
            final double x1 = obs[0] - MARGEM_OBSTACULO_CM;
            final double y1 = obs[1] - MARGEM_OBSTACULO_CM;
            final double x2 = obs[2] + MARGEM_OBSTACULO_CM;
            final double y2 = obs[3] + MARGEM_OBSTACULO_CM;
            if (px >= x1 && px <= x2 && py >= y1 && py <= y2) return true;
        }
        return false;
    }
}