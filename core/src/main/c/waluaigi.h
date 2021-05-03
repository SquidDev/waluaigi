#ifndef wa_h
#define wa_h

#include "lua/lapi.h"
#include "lua/ldo.h"

/*******************************************************************************
 * Error handling
 ******************************************************************************/

/** Try to invoke function f with arguments L and ud */
void waluaigi_try(lua_State *L, Pfunc f, void *ud);

/** Throw an exception, to be caught by waluaigi_try */
void waluaigi_throw();

/*******************************************************************************
 * Timeouts
 ******************************************************************************/

#define WA_TIMEOUT_OK 0     // No issues
#define WA_TIMEOUT_PAUSE 1  // Attempt to pause the VM
#define WA_TIMEOUT_ERROR 2  // Throw an error
#define WA_TIMEOUT_HALT 3   // Abort immediately.

/** Get the current timeout state.
 *
 * This catches WA_TIMEOUT_HALT internally, so that status does not need to be
 * handled by callers
 */
int waluaigi_get_timeout();

#endif
