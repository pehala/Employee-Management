vacation service
================
REST API
--------
* [POST] `/api/vacation/create`\
  Creates new vacation. Requires admin rights.
* [PUT] `/api/vacation/<id>/update`\
  Updates vacation with given `id`. Requires admin rights.
* [DELETE] `/api/vacation/<id>/delete`\
  Removes vacation with given `id`. Requires admin rights.
* [GET] `/api/vacation/<id>`\
  Returns vacation with given `id`.
* [GET] `/api/vacation/`\
  Returns all vacations. Requires admin rights.
* [GET] `/api/vacation/employee/<id>`\
  Query params: Dates `from` and `to`.\
  Returns vacation of employee with given `id` within dates `from` to `to`.
  If the employee has no vacation within given range, then empty list is returned.
* [GET] `/api/vacation/employee/<id>/date`\
  Query param: `date`.\
  Returns `true` if employee with given `id` has vacation in specified `date`, `false` otherwise.