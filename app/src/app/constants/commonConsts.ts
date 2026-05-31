export const ResponseTypeColor = {
    SUCCESS: 1,
    WARNING: 2,
    INFO: 3,
    ERROR: 4
}

export type ResponseTypeColor = typeof ResponseTypeColor[keyof typeof ResponseTypeColor];